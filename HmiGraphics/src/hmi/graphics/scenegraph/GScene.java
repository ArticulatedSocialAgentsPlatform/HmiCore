/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2015 University of Twente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/

package hmi.graphics.scenegraph;

import hmi.animation.VJointUtils;
import hmi.math.Mat4f;
import hmi.math.Quat4f;
import hmi.math.Vec3f;
import hmi.util.BinUtil;
import hmi.util.Diff;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A GScene combines a GNode based scene graph with other scene elements, like
 * Controllers, ...
 * A scene graph is really a forest, containing (potentially) several root nodes.
 * @author Job Zwiers
 */
public class GScene extends XMLStructureAdapter implements Diff.Differentiable
{

    private String id;
    private List<GNode> rootNodes = new ArrayList<GNode>();
    // We need a list of skinned meshes that reside somewhere inside the scene graph, so we can deform/update when needed.
    // The list is collected/resolved from a scenegraph
    private List<GSkinnedMesh> skinnedMeshList = new ArrayList<GSkinnedMesh>();
    // skeletonRoots are resolved from skinnedMeshList and rootNodes.
    private List<GNode> skeletonRoots = new ArrayList<GNode>();
    private String xmlVersion = "0.0";

    private static Logger logger = LoggerFactory.getLogger(GScene.class.getName());

    /**
     * Creates a new GScene.
     */
    public GScene(String id)
    {
        this.id = id == null ? "" : id.intern();
        // addOriginMarker();
    }

    public GScene(XMLTokenizer tokenizer) throws IOException
    {
        this("");
        readXML(tokenizer);
    }

    /**
     * returns the id of tghis GScene
     */
    public String getId()
    {
        return id;
    }

    /**
     * show differences
     */
    public String showDiff(Object gsceneObj)
    {
        GScene gscene = (GScene) gsceneObj;
        if (gscene == null) return "GScene diff: null GScene";
        String diff = Diff.showDiff("GScene diff, rootNodes", rootNodes, gscene.rootNodes);
        if (diff != "") return diff;
        diff = Diff.showDiff("GScene diff skinnedMeshList", skinnedMeshList, gscene.skinnedMeshList);
        if (diff != "") return diff;
        diff = Diff.showDiff("GScene diff name", skeletonRoots, gscene.skeletonRoots);
        if (diff != "") return diff;
        diff = Diff.showDiff("GScene  diff xmlVersion", xmlVersion, gscene.xmlVersion);
        if (diff != "") return diff;
        return "";
    }

    /**
     * Adds a GNode as a root node to the scene graph forest.
     */
    public void addRootNode(GNode root)
    {
        logger.debug("GScene.addRootNode " + root.getId());
        rootNodes.add(root);
    }

    /**
     * returns the list with the root nodes of the scene graph forrest. non-null
     */
    public List<GNode> getRootNodes()
    {
        return rootNodes;
    }

    public List<GNode> getSkeletonRoots()
    {
        return skeletonRoots;
    }

    /**
     * returns the (GNode) root node with specified sid, or null, if not present
     */
    public GNode getPartBySid(String sid)
    {
        GNode result = null;
        for (GNode rootNode : getRootNodes())
        {
            result = rootNode.getPartBySid(sid);
            if (result != null) return result;
        }
        return result;
    }

    /**
     * Searches all GSkinnedMeshes within the scene, and adds them to skinnedMeshList
     */
    public void collectSkinnedMeshes()
    {
        skinnedMeshList = new ArrayList<GSkinnedMesh>();

        for (GNode rtnode : rootNodes)
        {
            List<GSkinnedMesh> skinnedList = new ArrayList<GSkinnedMesh>();
            collectSkinnedMeshes(rtnode, skinnedList);
            skinnedMeshList.addAll(skinnedList);
        }
    }

    /*
     * Searches all GSkinnedMeshes within the tree rooted at gnode, and adds them to skinnedList
     */
    private void collectSkinnedMeshes(GNode gnode, List<GSkinnedMesh> skinnedList)
    {
        List<GShape> gshapes = gnode.getGShapes(); // might be empty, but not null
        for (GShape gshape : gshapes)
        {
            GMesh gmesh = gshape.getGMesh();
            if (gmesh instanceof GSkinnedMesh) skinnedList.add((GSkinnedMesh) gmesh);
        }
        List<GNode> childNodes = gnode.getChildNodes(); // might be empty, but not null
        for (GNode child : childNodes)
        {
            collectSkinnedMeshes(child, skinnedList);
        }
    }

    /**
     * Adds some SkinnedMeshes
     */
    public void addSkinnedMeshes(List<GSkinnedMesh> skinnedMeshes)
    {
        // skinnedMeshList.addAll(skinnedMeshes);
    }

    /**
     * returns the List of all GSkinnedMeshes for this GScene.
     */
    public List<GSkinnedMesh> getSkinnedMeshes()
    {
        return skinnedMeshList;
    }

    /**
     * Resolves the VJoints for all skinned meshes within this GScene,
     */
    public void resolveSkinnedMeshJoints()
    {
        logger.debug("GScene.resolveSkinnedMeshJoints");
        for (GSkinnedMesh gsm : skinnedMeshList)
        {
            gsm.resolveJoints(rootNodes);
            GNode[] meshSkeletonRoots = gsm.getSkeletonRoots();
            if (meshSkeletonRoots == null) return;
            for (int i = 0; i < meshSkeletonRoots.length; i++)
            {
                GNode skelRoot = meshSkeletonRoots[i];
                if (!skeletonRoots.contains(skelRoot)) skeletonRoots.add(skelRoot);
            }
        }
    }

    /**
     * Calculates the matrices for the VJoint structures.
     */
    public void calculateVJointMatrices()
    {
        // we may assume that VJoints are linked together.
        for (GNode root : rootNodes)
        {
            root.getVJoint().calculateMatrices();
        }
    }

    public void clearRotations()
    {
        for (GNode skelRoot : skeletonRoots)
        {
            skelRoot.clearRotations();
        }
    }

    public void rotate(float x, float y, float z, float angle)
    {
        rotateScale(x, y, z, angle, 1.0f);
    }

    public void scale(float scale)
    {
        rotateScale(1f, 0f, 0f, 0f, scale);
    }

    public void rotateScale(float x, float y, float z, float angle, float scale)
    {
        float[] q = Quat4f.getQuat4f();
        Quat4f.setFromAxisAngle4f(q, x, y, z, angle);
        float[] t = Vec3f.getZero();
        // t[0] = 3.0f;
        // float[] t = Vec3f.getVec3f(0f, 0f, 94.701f);
        affineTransform(t, q, scale);
    }

    public void affineTransform(float[] t, float[] q, float s)
    {
        float[] mat4x4 = Mat4f.getMat4f();
        float[] zerovec = Vec3f.getZero();
        Mat4f.setFromTRS(mat4x4, zerovec, q, s);

        for (GNode rootNode : rootNodes)
        {
            rootNode.affineTransform(mat4x4);
        }
    }

    /**
     * rename joint sids.
     */
    public void renameJoints(Map<String, String> renaming)
    {
        for (GNode rnode : skeletonRoots)
            rnode.renameJoints(renaming);
        for (GSkinnedMesh skm : skinnedMeshList)
            skm.renameJointSIDs(renaming);
    }

    public void adjustBindPoses()
    {
        for (GSkinnedMesh gsm : skinnedMeshList)
        {
            gsm.simplifyBindPose();
        }
    }

    /**
     * Ensures that all GMeshes, including all GSkinnedMeshes, have unified indices and have Triangle type.
     */
    public void normalizeMeshes()
    {
        for (GNode rootGNode : getRootNodes())
        {
            normalizeMeshes(rootGNode);
        }
    }

    /**
     * Ensures that all GMeshes, including all GSkinnedMeshes, inside the tree with gnode as its root,
     * have unified indices and have Triangle type.
     */
    public void normalizeMeshes(GNode gnode)
    {
        for (GShape gshape : gnode.getGShapes())
        {
            GMesh gmesh = gshape.getGMesh();
            if (!gmesh.hasUnifiedIndexData()) gmesh.unifyIndices();
            if (gmesh.getMeshType() == GMesh.MeshType.Polylist || gmesh.getMeshType() == GMesh.MeshType.Polygons)
            {
                gmesh.triangulate();
            }
        }
        for (GNode gchild : gnode.getChildNodes())
            normalizeMeshes(gchild);
    }

    public void analyzeMeshes()
    {
        for (GNode rootGNode : getRootNodes())
        {
            analyzeMeshes(rootGNode);
        }
    }

    public void analyzeMeshes(GNode gnode)
    {
        for (GShape gshape : gnode.getGShapes())
        {
            GMesh gmesh = gshape.getGMesh();
            logger.debug("Analyze mesh " + gmesh.getId());
            gmesh.analyze();

        }
        for (GNode gchild : gnode.getChildNodes())
            analyzeMeshes(gchild);
    }

    /**
     * Sets all skeletons into HAnim pose
     */
    public void setSkeletonHAnimPoses()
    {

        for (GNode skeletonRoot : skeletonRoots)
        {
            VJointUtils.setHAnimPose(skeletonRoot.getVJoint());
        }
        calculateVJointMatrices();
        Skeletons.setNeutralPoses(skeletonRoots, skinnedMeshList, rootNodes);

    }

    /**
     * appends XML attributes to buf.
     */
    @Override
    public StringBuilder appendAttributes(StringBuilder buf)
    {
        super.appendAttributes(buf);
        appendAttribute(buf, "version", xmlVersion);
        appendAttribute(buf, "id", id);
        return buf;
    }

    /**
     * decodes XML attributes.
     */
    @Override
    public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer)
    {
        xmlVersion = getOptionalAttribute("version", attrMap, "0.0");
        id = getOptionalAttribute("id", attrMap, "");
        super.decodeAttributes(attrMap, tokenizer);
    }

    // private List<GNode> rootNodes = new ArrayList<GNode>();
    // // We need a list of skinned meshes that reside somewhere inside the scene graph, so we can deform/update when needed.
    // private List<GSkinnedMesh> skinnedMeshList = new ArrayList<GSkinnedMesh>();
    // private List<GNode> skeletonRoots = new ArrayList<GNode>();
    // private String xmlVersion = "0.0";

    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt)
    {
        appendSTag(buf, "rootnodes", fmt);
        appendXMLStructureList(buf, fmt, rootNodes);
        appendETag(buf, "rootnodes", fmt);
        return buf;
    }

    @Override
    public void decodeContent(XMLTokenizer tokenizer) throws IOException
    {
        while (tokenizer.atSTag())
        {
            String tag = tokenizer.getTagName();
            if (tag.equals("rootnodes"))
            {
                tokenizer.takeSTag("rootnodes");
                rootNodes = (List<GNode>) decodeXMLStructureList(tokenizer, "gnode", GNode.class);
                tokenizer.takeETag("rootnodes");
                // instanceVisualScene = new InstanceVisualScene(getCollada(), tokenizer);
                // } else if (tag.equals("skeletonroots")) {
                // tokenizer.takeSTag("skeletonroots");
                // skeletonRoots = (List<GNode>) decodeXMLStructureList(tokenizer, "gnode", GNode.class) ;
                // tokenizer.takeETag("skeletonroots");
                // } else if (tag.equals("skinnedmeshes")) {
                // tokenizer.takeSTag("skinnedmeshes");
                // skinnedMeshList = (List<GSkinnedMesh>) decodeXMLStructureList(tokenizer, "gskinnedmesh", GSkinnedMesh.class) ;
                // tokenizer.takeETag("skinnedmeshes");
            }
            else
            {
                System.out.println(tokenizer.getErrorMessage("Scene: skip : " + tokenizer.getTagName()));
                tokenizer.skipTag();
            }
        }

    }

    /*
     * The XML Stag for XML encoding
     */
    private static final String XMLTAG = "gscene";

    /**
     * The XML Stag for XML encoding
     */
    public static String xmlTag()
    {
        return XMLTAG;
    }

    /**
     * returns the XML Stag for XML encoding
     */
    @Override
    public String getXMLTag()
    {
        return XMLTAG;
    }

    /**
     * Writes a binary encoding to dataOut
     */
    public void writeBinary(DataOutput dataOut) throws IOException
    {
        BinUtil.writeOptionalString(dataOut, xmlVersion);
        BinUtil.writeBinaryList(dataOut, getRootNodes());
    }

    /**
     * Reads a binary encoding from dataIn
     */
    public void readBinary(DataInput dataIn) throws IOException
    {
        xmlVersion = BinUtil.readOptionalString(dataIn);
        ArrayList<GNode> rnodes = BinUtil.readBinaryList(dataIn, GNode.class);
        if (rnodes == null)
        {
            rootNodes = null;
        }
        else
        {
            for (GNode rn : rnodes)
                addRootNode(rn);
        }
    }

}
