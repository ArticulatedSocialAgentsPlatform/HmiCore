package hmi.facegraphics;

import hmi.faceanimation.model.Eye;
import hmi.faceanimation.model.FAP;
import hmi.faceanimation.model.FeaturePoint;
import hmi.faceanimation.model.Head;
import hmi.faceanimation.model.LowerJaw;
import hmi.faceanimation.model.MPEG4;
import hmi.faceanimation.model.Neck;
import hmi.facegraphics.deformers.Deformer;
import hmi.facegraphics.deformers.EaseDeformer;
import hmi.facegraphics.deformers.SmoothDeformer;
import hmi.graphics.opengl.GLSkinnedMesh;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

public class GLHead extends XMLStructureAdapter implements Head
{
    private static final long serialVersionUID = -1151644062991823029L;
    private HashMap<FeaturePoint, float[]> fpPositions;
    private ArrayList<FAP> keepSync;
    private HashMap<FAP, Deformer> deformers;
    private HashMap<FAP, ArrayList<Integer>> vertexMasks; // XXX could be replaced by a multimap
    private HashMap<String, Float> fapus;
    private boolean fapusDirty;
    private float xcenter, xsize;
    private Eye leftEye, rightEye;
    private LowerJaw lowerJaw;
    private Neck neck;
    // private VGLNode bodyNode;

    /**
     * This map contains maps, each of which contains pairs of vertex-index - displacement.
     */
    private HashMap<FAP, HashMap<Integer, float[]>> displacements; // XXX could be replaced by a multimap
    private boolean displacementsDirty = false;
    private boolean deformScheduled = false;
    private float[] vertexData;
    private float[] neutralVertexData;
    private GLSkinnedMesh faceMesh;

    private enum Operation
    {
        SUM, AVERAGE
    };

    private final Operation mergeOp = Operation.SUM;

    public GLHead()
    {
        fpPositions = new HashMap<FeaturePoint, float[]>();
        keepSync = new ArrayList<FAP>();
        deformers = new HashMap<FAP, Deformer>();
        vertexMasks = new HashMap<FAP, ArrayList<Integer>>();
        fapus = new HashMap<String, Float>();
        fapusDirty = true;

        displacements = new HashMap<FAP, HashMap<Integer, float[]>>();
    }

    public float[] getFPPosition(FeaturePoint fp)
    {
        if (!fpPositions.containsKey(fp)) return null;
        else return fpPositions.get(fp);
    }

    private float[] getFPPosition(String fpString)
    {
        return getFPPosition(MPEG4.getFeaturePoint(fpString));
    }

    public void setFPPosition(FeaturePoint fp, float[] pos)
    {
        fpPositions.put(fp, pos);
        fapusDirty = true;
    }

    public void setKeepSync(FAP fap, boolean sync)
    {
        if (sync && !keepSync.contains(fap)) keepSync.add(fap);
        else if (!sync && keepSync.contains(fap)) keepSync.remove(fap);
    }

    public boolean getKeepSync(FAP fap)
    {
        return keepSync.contains(fap);
    }

    public void setDeformer(FAP fap, Deformer deformer)
    {
        deformers.put(fap, deformer);
    }

    public Deformer getDeformer(FAP fap)
    {
        if (!deformers.containsKey(fap))
        {
            EaseDeformer deformer = new EaseDeformer();
            deformer.setHead(this);
            deformer.setFAP(fap);

            deformers.put(fap, deformer);
            return (Deformer) deformer;
        }
        else
        {
            Deformer deformer = (Deformer) deformers.get(fap);
            return deformer;
        }
    }

    public ArrayList<Integer> getVertexMask(FAP fap)
    {
        if (!vertexMasks.containsKey(fap))
        {
            ArrayList<Integer> vertexMask = new ArrayList<Integer>();
            vertexMasks.put(fap, vertexMask);
            return vertexMask;
        }
        else return vertexMasks.get(fap);
    }

    public float getFAPU(String fapu)
    {
        calculateFAPUs();
        if (fapus.containsKey(fapu)) return fapus.get(fapu);
        else return 0.0f;
    }

    private void calculateFAPUs()
    {
        final int STEPS = 1024;

        if (!fapusDirty) return;
        fapusDirty = false;

        // AU (predefined as 10-5 rad)
        fapus.put("AU", 1E-4f);

        // ES0 (between center of eyes)
        float[] eyeLeft = getFPPosition("3.5");
        float[] eyeRight = getFPPosition("3.6");
        float[] es0Center = null;
        if (eyeLeft != null && eyeRight != null)
        {
            fapus.put("ES", calculateDistance(eyeLeft, eyeRight) / STEPS);
            es0Center = calculateCenter(eyeLeft, eyeRight);
        }

        // IRISD0
        float[] upperLid = getFPPosition("3.1");
        float[] lowerLid = getFPPosition("3.3");
        if (upperLid != null && lowerLid != null) fapus.put("IRISD", calculateDistance(upperLid, lowerLid) / STEPS);

        // ENS0
        float[] noseBottom = getFPPosition("9.15");
        if (es0Center != null && noseBottom != null) fapus.put("ENS", calculateDistance(es0Center, noseBottom) / STEPS);

        // MNS0
        float[] mouthTop = getFPPosition("8.1");
        if (noseBottom != null && mouthTop != null) fapus.put("MNS", calculateDistance(noseBottom, mouthTop) / STEPS);

        // MW0
        float[] mouthLeft = getFPPosition("8.3");
        float[] mouthRight = getFPPosition("8.4");
        if (mouthLeft != null && mouthRight != null) fapus.put("MW", calculateDistance(mouthLeft, mouthRight) / STEPS);
    }

    private float calculateDistance(float[] first, float[] second)
    {
        float dx = first[0] - second[0];
        float dy = first[1] - second[1];
        float dz = first[2] - second[2];

        float dist = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        return dist;
    }

    private float[] calculateCenter(float[] first, float[] second)
    {
        float dx = first[0] - second[0];
        float dy = first[1] - second[1];
        float dz = first[2] - second[2];

        float[] center = new float[3];
        center[0] = first[0] + dx / 2;
        center[1] = first[1] + dy / 2;
        center[2] = first[2] + dz / 2;

        return center;
    }

    public int getNumVertices()
    {
        return vertexData.length / 3;
    }

    public float[] getVertexData()
    {
        vertexData = faceMesh.getVertexData(0, vertexData);
        return vertexData;
    }

    private void calculateHeadWidth()
    {
        int numVertices = vertexData.length / 3;
        float x, xmin = 0.0f, xmax = 0.0f;
        for (int i = 0; i < numVertices; i++)
        {
            x = vertexData[i * 3];
            xmin = Math.min(xmin, x);
            xmax = Math.max(xmax, x);
        }

        xsize = xmax - xmin;
        xcenter = xmin + xsize / 2;
    }

    public float getWidth()
    {
        return xsize;
    }

    public float getXCenter()
    {
        return xcenter;
    }

    private void setNeutralVertexData(float[] neutralVertexData)
    {
        this.neutralVertexData = neutralVertexData;
    }

    public float[] getNeutralVertexPos(int index)
    {
        int vindex = index * 3;
        float[] retval = { neutralVertexData[vindex], neutralVertexData[vindex + 1], neutralVertexData[vindex + 2] };
        return retval;
    }

    public void setFaceMesh(GLSkinnedMesh faceMesh)
    {
        this.faceMesh = faceMesh;
        getVertexData();
        setNeutralVertexData(vertexData.clone());
        calculateHeadWidth();
        calculateVertexWeights();
    }

    public void calculateVertexWeights()
    {
        // Enable GLSkinnedMesh FAP-steering
        faceMesh.setUseFaps(true);

        // Set direction vectors, this is a floar[nrOfFaps][3] array.
        HashMap<Integer, FAP> faps = MPEG4.getFAPs();
        int nrOfFaps = faps.size();
        float[][] directionVectors = new float[nrOfFaps][3];
        for (FAP fap : faps.values())
        {
            directionVectors[fap.index] = getDeformer(fap).getFullDisplacement();
        }

        faceMesh.setFapDirectionVectors(directionVectors);

        /*
         * Set vertex-to-fap bindings. Per vertex, we must know: - number of FAPs possibly having an influence on this vertex - indices of these FAPs
         * - weights of these FAP <> vertex influence
         * 
         * We first walk over all the deformers and ask them to give us vertex-indices and corresponding weights.
         */

        int numVertices = getNumVertices();
        int[] fapCount = new int[numVertices];
        TreeMap<Integer, ArrayList<Integer>> fapIndices = new TreeMap<Integer, ArrayList<Integer>>();
        TreeMap<Integer, ArrayList<Float>> fapWeights = new TreeMap<Integer, ArrayList<Float>>();
        int numInfluences = 0;
        HashMap<FAP, HashMap<Integer, Float>> fapDeformerWeights = new HashMap<FAP, HashMap<Integer, Float>>();
        for (FAP fap : faps.values())
        {
            HashMap<Integer, Float> deformerWeights = getDeformer(fap).getVertexWeights();
            fapDeformerWeights.put(fap, deformerWeights);
            for (int vertexIndex : deformerWeights.keySet())
            {
                fapCount[vertexIndex]++;

                if (fapIndices.get(vertexIndex) == null) fapIndices.put(vertexIndex, new ArrayList<Integer>());
                fapIndices.get(vertexIndex).add(fap.index);

                float weight = deformerWeights.get(vertexIndex);
                if (fapWeights.get(vertexIndex) == null) fapWeights.put(vertexIndex, new ArrayList<Float>());
                fapWeights.get(vertexIndex).add(weight);

                numInfluences++;
            }
        }

        // Now we have all FAP-indices and weights ordered by vertex-index, we can
        // flatten this structure to obtain some simple arrays.
        int[] fapIndex = new int[numInfluences];
        float[] fapWeight = new float[numInfluences];

        int currentInfluence = 0;
        for (ArrayList<Integer> indices : fapIndices.values())
        {
            for (int index : indices)
                fapIndex[currentInfluence++] = index;
        }

        currentInfluence = 0;
        for (ArrayList<Float> weights : fapWeights.values())
        {
            for (float weight : weights)
                fapWeight[currentInfluence++] = weight;
        }

        faceMesh.setFapVertexWeights(fapCount, fapIndex, fapWeight);

        scheduleDeform();
    }

    public void scheduleDeform()
    {
        deformScheduled = true;
    }

    public void deformWhenScheduled()
    {
        if (!deformScheduled) return;

        HashMap<Integer, FAP> faps = MPEG4.getFAPs();
        int nrOfFaps = faps.size();

        float[] amplitudes = new float[nrOfFaps];
        for (FAP fap2 : faps.values())
        {
            amplitudes[fap2.index] = ((float) getDeformer(fap2).getValue()) / 1024;
        }

        faceMesh.setFapAmplitudes(amplitudes);
        faceMesh.deform();

        if (leftEye != null) leftEye.copy();
        if (rightEye != null) rightEye.copy();
        if (neck != null) neck.copy();
        if (lowerJaw != null) lowerJaw.copy();
    }

    @Deprecated
    public void setDisplacements(FAP fap, HashMap<Integer, float[]> disp)
    {
        // displacements.put(fap, disp);
        // displacementsDirty = true;

        HashMap<Integer, FAP> faps = MPEG4.getFAPs();
        int nrOfFaps = faps.size();

        float[] amplitudes = new float[nrOfFaps];
        for (FAP fap2 : faps.values())
        {
            amplitudes[fap2.index] = ((float) getDeformer(fap2).getValue()) / 1024;
        }

        faceMesh.setFapAmplitudes(amplitudes);
        faceMesh.deform();
    }

    public void clearDisplacements()
    {
        for (FAP fap : MPEG4.getFAPs().values())
        {
            getDeformer(fap).setValue(0);
        }
    }

    @Deprecated
    public void applyDisplacements()
    {
        if (!displacementsDirty) return;
        displacementsDirty = false;

        // Create a structure where all displacements are ordered by vertex-index.
        HashMap<Integer, ArrayList<float[]>> flat = new HashMap<Integer, ArrayList<float[]>>();
        for (HashMap<Integer, float[]> disp : displacements.values())
        {
            for (Entry<Integer, float[]> entry : disp.entrySet())
            {
                Integer index = entry.getKey();
                if (!flat.containsKey(index)) flat.put(index, new ArrayList<float[]>());

                flat.get(index).add(disp.get(index));
            }
        }

        // Create a structure where all displacements are merged by vertex-index.
        HashMap<Integer, float[]> merged = new HashMap<Integer, float[]>();
        for (int index : flat.keySet())
        {
            ArrayList<float[]> vdisps = flat.get(index); // Vertex-displacements.

            float x = 0.0f;
            float y = 0.0f;
            float z = 0.0f;
            for (float[] vdisp : vdisps)
            {
                x += vdisp[0];
                y += vdisp[1];
                z += vdisp[2];
            }

            if (mergeOp.equals(Operation.AVERAGE))
            {
                int num = vdisps.size();
                x /= num;
                y /= num;
                x /= num;
            }

            merged.put(index, new float[] { x, y, z });
        }

        // Iterate over all vertices (in vertexData) and displace them
        // or set them to their neutral position.
        for (int vindex = 0; vindex < vertexData.length; vindex += 3) // Vertex-index
        {
            int index = vindex / 3;

            float[] npos = new float[3];
            if (merged.containsKey(index))
            {
                npos[0] = neutralVertexData[vindex] + merged.get(index)[0];
                npos[1] = neutralVertexData[vindex + 1] + merged.get(index)[1];
                npos[2] = neutralVertexData[vindex + 2] + merged.get(index)[2];
            }
            else
            {
                npos[0] = neutralVertexData[vindex];
                npos[1] = neutralVertexData[vindex + 1];
                npos[2] = neutralVertexData[vindex + 2];
            }

            vertexData[vindex] = npos[0];
            vertexData[vindex + 1] = npos[1];
            vertexData[vindex + 2] = npos[2];
        }

        // Apply it to the face
        faceMesh.setVertexData(0, vertexData);
    }

    public void setEyes(Eye leftEye, Eye rightEye)
    {
        this.leftEye = leftEye;
        this.rightEye = rightEye;
    }

    public Eye getLeftEye()
    {
        return leftEye;
    }

    public Eye getRightEye()
    {
        return rightEye;
    }

    public void setLowerJaw(LowerJaw lowerJaw)
    {
        this.lowerJaw = lowerJaw;
    }

    public LowerJaw getLowerJaw()
    {
        return lowerJaw;
    }

    public void setNeck(Neck neck)
    {
        this.neck = neck;
    }

    public Neck getNeck()
    {
        return neck;
    }

    /*
     * public void setBodyNode(VGLNode bodyNode)
     * 
     * { this.bodyNode = bodyNode; }
     * 
     * public VGLNode getBodyNode() { return bodyNode; }
     */
    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt)
    {
        int index;
        int[] ints;

        /*
         * Append content for feature point positions.
         */
        appendSTag(buf, "fp-positions", fmt);
        for (FeaturePoint fp : fpPositions.keySet())
        {
            HashMap<String, String> attrs = new HashMap<String, String>();
            attrs.put("fp", fp.toString());
            float[] pos = fpPositions.get(fp);
            attrs.put("x", Float.toString(pos[0]));
            attrs.put("y", Float.toString(pos[1]));
            attrs.put("z", Float.toString(pos[2]));
            appendEmptyTag(buf, fmt, "fp-position", attrs);
        }
        appendETag(buf, "fp-positions", fmt);

        /*
         * Append information about for what FAPs, the keepSync feature is on.
         */
        appendSTag(buf, "keep-syncs", fmt);
        ints = new int[keepSync.size()];
        index = 0;
        for (FAP fap : keepSync)
        {
            if (fap == null) continue;
            ints[index++] = fap.getNumber();
        }
        appendInts(buf, ints, ' ', fmt, 20);
        appendETag(buf, "keep-syncs", fmt);

        /*
         * Append deformers and their parameters.
         */
        appendSTag(buf, "deformers", fmt);
        for (FAP fap : deformers.keySet())
        {
            if (fap == null) continue;
            Deformer deformer = deformers.get(fap);
            deformer.appendContent(buf, fmt);
        }
        appendETag(buf, "deformers", fmt);

        /*
         * Append vertex masks.
         */
        appendSTag(buf, "vertex-masks", fmt);
        for (FAP fap : vertexMasks.keySet())
        {
            if (fap == null) continue;
            ArrayList<Integer> list = vertexMasks.get(fap);
            if (list.size() == 0) continue;

            HashMap<String, String> attrs = new HashMap<String, String>();
            attrs.put("fap", Integer.toString(fap.getNumber()));
            appendSTag(buf, "vertex-mask", attrs);
            ints = new int[list.size()];
            index = 0;
            for (int vindex : list)
                ints[index++] = vindex;
            appendInts(buf, ints, ' ', fmt, 20);
            appendETag(buf, "vertex-mask", fmt);
        }
        appendETag(buf, "vertex-masks", fmt);

        return buf;
    }

    @Override
    public void decodeContent(XMLTokenizer tokenizer) throws IOException
    {
        int ints[];

        /*
         * Decode feature point posititions.
         */
        tokenizer.takeSTag("fp-positions");
        while (tokenizer.getTagName().equals("fp-position"))
        {
            HashMap<String, String> attrMap = tokenizer.getAttributes();
            String fp = getRequiredAttribute("fp", attrMap, tokenizer);
            Float x = getRequiredFloatAttribute("x", attrMap, tokenizer);
            Float y = getRequiredFloatAttribute("y", attrMap, tokenizer);
            Float z = getRequiredFloatAttribute("z", attrMap, tokenizer);
            fpPositions.put(MPEG4.getFeaturePoint(fp), new float[] { x, y, z });
            tokenizer.takeSTag("fp-position");
            tokenizer.takeETag("fp-position");
        }
        tokenizer.takeETag("fp-positions");

        /*
         * Decode information about for what FAPs, the keepSync feature is on.
         */
        tokenizer.takeSTag("keep-syncs");
        if (tokenizer.atCharData())
        {
            ints = decodeIntArray(tokenizer.getTrimmedCharData(), " " + '\n' + '\r');
            for (int index = 0; index < ints.length; index++)
            {
                keepSync.add(MPEG4.getFAP(ints[index]));
            }
            tokenizer.takeCharData();
        }
        tokenizer.takeETag("keep-syncs");

        /*
         * Decode deformers and their parameters.
         */
        tokenizer.takeSTag("deformers");
        while (true)
        {
            Deformer deformer = null;
            if (tokenizer.getTagName().equals("ease-deformer"))
            {
                deformer = new EaseDeformer();
            }
            else if (tokenizer.getTagName().equals("smooth-deformer"))
            {
                deformer = new SmoothDeformer();
            }
            else
            {
                break;
            }

            deformer.decodeContent(tokenizer);
            deformer.setHead(this);
            deformers.put(deformer.getFAP(), deformer);
        }
        tokenizer.takeETag("deformers");

        /*
         * Decode vertex masks.
         */
        tokenizer.takeSTag("vertex-masks");
        while (tokenizer.getTagName().equals("vertex-mask"))
        {
            HashMap<String, String> attrMap = tokenizer.getAttributes();
            tokenizer.takeSTag("vertex-mask");

            FAP fap = MPEG4.getFAP(getRequiredIntAttribute("fap", attrMap, tokenizer));
            vertexMasks.put(fap, new ArrayList<Integer>());
            ints = decodeIntArray(tokenizer.getTrimmedCharData(), " " + '\n' + '\r');
            for (int index = 0; index < ints.length; index++)
            {
                vertexMasks.get(fap).add(ints[index]);
            }

            tokenizer.takeCharData();
            tokenizer.takeETag("vertex-mask");
        }
        tokenizer.takeETag("vertex-masks");
    }

    /*
     * The XML Stag for XML encoding
     */
    private static final String XMLTAG = "faceeditor-parameters";

    /**
     * The XML Stag for XML encoding -- use this static method when you want to see if a given String equals the xml tag for this class
     */
    public static String xmlTag()
    {
        return XMLTAG;
    }

    /**
     * The XML Stag for XML encoding -- use this method to find out the run-time xml tag of an object
     */
    @Override
    public String getXMLTag()
    {
        return XMLTAG;
    }
}
