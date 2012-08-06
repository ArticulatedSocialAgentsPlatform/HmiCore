package hmi.facegraphics.deformers;

import hmi.faceanimation.model.MPEG4;
import hmi.math.Bezier2f;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.HashMap;

public class SmoothDeformer extends Deformer implements SmoothDeformerServer
{
    private float scalex, scaley, scalez;
    private int smoothCenter, smoothSide;
    private boolean useVertexMask, invertVertexMask;
    private Bezier2f bez;

    protected SmoothDeformerClient client;

    public SmoothDeformer()
    {
        smoothCenter = 0;
        smoothSide = 0;
        scalex = 1.0f;
        scaley = 1.0f;
        scalez = 1.0f;
    }

    public void updateSmoothCenter(int smoothCenter)
    {
        this.smoothCenter = smoothCenter;
        bez = null;
        head.calculateVertexWeights();
        deform();
    }

    private void setSmoothCenter(int smoothCenter)
    {
        updateSmoothCenter(smoothCenter);
        emitSmoothCenter(smoothCenter);
    }

    public int getSmoothCenter()
    {
        return smoothCenter;
    }

    public void updateSmoothSide(int smoothSide)
    {
        this.smoothSide = smoothSide;
        bez = null;
        head.calculateVertexWeights();
        deform();
    }

    private void setSmoothSide(int smoothSide)
    {
        updateSmoothSide(smoothSide);
        emitSmoothSide(smoothSide);
    }

    public int getSmoothSide()
    {
        return smoothSide;
    }

    public void updateScalex(float scale)
    {
        scalex = scale;
        head.calculateVertexWeights();
        deform();
    }

    private void setScalex(float scale)
    {
        updateScalex(scale);
        emitScalex(scale);
    }

    public float getScalex()
    {
        return scalex;
    }

    public void updateScaley(float scale)
    {
        scaley = scale;
        head.calculateVertexWeights();
        deform();
    }

    private void setScaley(float scale)
    {
        updateScaley(scale);
        emitScaley(scale);
    }

    public float getScaley()
    {
        return scaley;
    }

    public void updateScalez(float scale)
    {
        scalez = scale;
        head.calculateVertexWeights();
        deform();
    }

    private void setScalez(float scale)
    {
        updateScalez(scale);
        emitScalez(scale);
    }

    public float getScalez()
    {
        return scalez;
    }

    public void updateUseVM(boolean value)
    {
        this.useVertexMask = value;
        head.calculateVertexWeights();
        deform();
    }

    private void setUseVM(boolean value)
    {
        updateUseVM(value);
        emitUseVM(value);
    }

    public boolean getUseVM()
    {
        return useVertexMask;
    }

    public void updateInvertVM(boolean value)
    {
        this.invertVertexMask = value;
        head.calculateVertexWeights();
        deform();
    }

    private void setInvertVM(boolean value)
    {
        updateInvertVM(value);
        emitInvertVM(value);
    }

    public boolean getInvertVM()
    {
        return invertVertexMask;
    }

    @Override
    float getWeight(int index)
    {
        boolean inVM = head.getVertexMask(fap).contains(index);
        if (invertVertexMask)
            inVM = !inVM;
        if (useVertexMask && !inVM)
            return 0f;

        // Distance between neutral position of vertex and original feature point position.
        float[] npos = head.getNeutralVertexPos(index);
        float dist = getDistance(npos, fpNeutralPos, scalex, scaley, scalez);

        float x = (dist / size) - (smoothCenter / 100f);

        if (bez == null)
        {
            // Init Bezier2f
            float[] p0 = new float[] { 0.0f - (smoothCenter / 100f), 1.0f };
            float[] v0 = new float[] { 0.2f + (smoothCenter / 100f), 0.0f };

            float[] p1 = new float[] { 0.5f, 0.5f };
            float[] v1 = new float[] { 0.2f, -0.2f };

            float[] p2 = new float[] { 1.0f + (smoothSide / 100f), 0.0f };
            float[] v2 = new float[] { 0.2f + (smoothSide / 100f), 0.0f };

            float[][] points = new float[][] { p0, p1, p2 };
            float[][] vectors = new float[][] { v0, v1, v2 };
            float[] weights = new float[] { 1.0f, 1.0f, 1.0f };

            bez = Bezier2f.bezier2fFromPointsVectorsSingleWeights(points, vectors, weights);

            // Print data so we can plot it for debugging purposes
            // DecimalFormat df = new DecimalFormat("#.##");
            for (float i = -1.0f; i < 2.1f; i += 0.1f)
            {
                float y = bez.evalFX(i);

                System.out.println(i + ", " + y);
            }
            System.out.println("-----");
        }

        return bez.evalFX(x);
    }

    public void copyFrom(Deformer sourceIn)
    {
        if (sourceIn.getClass() != SmoothDeformer.class)
            return;
        SmoothDeformer source = (SmoothDeformer) sourceIn;
        setSize(source.size);
        setSmoothCenter(source.smoothCenter);
        setSmoothSide(source.smoothSide);
        setScalex(source.scalex);
        setScaley(source.scaley);
        setScalez(source.scalez);
        setUseVM(source.useVertexMask);
        //setInvertVM(source.invertVertexMask);
    }

    private void emitSmoothCenter(int smoothCenter)
    {
        if (client != null)
            client.updateSmoothCenter(smoothCenter);
    }

    private void emitSmoothSide(int smoothSide)
    {
        if (client != null)
            client.updateSmoothCenter(smoothSide);
    }

    private void emitScalex(float scale)
    {
        if (client != null)
            client.updateScalex(scale);
    }

    private void emitScaley(float scale)
    {
        if (client != null)
            client.updateScaley(scale);
    }

    private void emitScalez(float scale)
    {
        if (client != null)
            client.updateScalez(scale);
    }

    private void emitUseVM(boolean value)
    {
        if (client != null)
            client.updateUseVM(value);
    }

    private void emitInvertVM(boolean value)
    {
        if (client != null)
            client.updateInvertVM(value);
    }

    /*
     * Persistence methods.
     */

    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt)
    {
        HashMap<String, String> attrs = new HashMap<String, String>();

        attrs.put("fap", Integer.toString(fap.getNumber()));
        attrs.put("size", Float.toString(size));
        attrs.put("x", Float.toString(fpNeutralPos[0]));
        attrs.put("y", Float.toString(fpNeutralPos[1]));
        attrs.put("z", Float.toString(fpNeutralPos[2]));
        attrs.put("scalex", Float.toString(scalex));
        attrs.put("scaley", Float.toString(scaley));
        attrs.put("scalez", Float.toString(scalez));
        attrs.put("smooth-center", Integer.toString(smoothCenter));
        attrs.put("smooth-side", Integer.toString(smoothSide));
        attrs.put("use-vertex-mask", Boolean.toString(useVertexMask));
        attrs.put("invert-vertex-mask", Boolean.toString(invertVertexMask));
        appendEmptyTag(buf, fmt, "smooth-deformer", attrs);

        return buf;
    }

    @Override
    public void decodeContent(XMLTokenizer tokenizer) throws IOException
    {
        HashMap<String, String> attrMap = tokenizer.getAttributes();
        fap = MPEG4.getFAP(getRequiredIntAttribute("fap", attrMap, tokenizer));
        size = getRequiredFloatAttribute("size", attrMap, tokenizer);
        Float x = getRequiredFloatAttribute("x", attrMap, tokenizer);
        Float y = getRequiredFloatAttribute("y", attrMap, tokenizer);
        Float z = getRequiredFloatAttribute("z", attrMap, tokenizer);
        fpNeutralPos = new float[] { x, y, z };

        scalex = getRequiredFloatAttribute("scalex", attrMap, tokenizer);
        scaley = getRequiredFloatAttribute("scaley", attrMap, tokenizer);
        scalez = getRequiredFloatAttribute("scalez", attrMap, tokenizer);
        smoothCenter = getRequiredIntAttribute("smooth-center", attrMap, tokenizer);
        smoothSide = getRequiredIntAttribute("smooth-side", attrMap, tokenizer);
        useVertexMask = getRequiredBooleanAttribute("use-vertex-mask", attrMap, tokenizer);
        invertVertexMask = getRequiredBooleanAttribute("invert-vertex-mask", attrMap, tokenizer);

        tokenizer.takeSTag("smooth-deformer");
        tokenizer.takeETag("smooth-deformer");
    }
}
