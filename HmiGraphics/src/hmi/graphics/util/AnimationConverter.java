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

package hmi.graphics.util;

import hmi.animation.ConfigList;
import hmi.animation.SkeletonInterpolator;
import hmi.graphics.collada.Collada;
import hmi.graphics.collada.Scene;
import hmi.graphics.collada.scenegraph.ColladaTranslator;
import hmi.graphics.scenegraph.GScene;
import hmi.graphics.scenegraph.GSkinnedMesh;
import hmi.math.Mat4f;
import hmi.math.Quat4f;
import hmi.xml.XMLTokenizer;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class for conversion of SkeletonInterpolator animation data, necessary when a Collada skeleton
 * needs adaptation of bind pose matrices, must be rotated into position, and must be put into HAnim pose
 * as its neutral position. The constructor reads a Collada file, supposed to include a Collada scene
 * containing the virtual character, including the skeleton to be used for the conversion data.
 * Thereafter, SkeletonInterpolators can be converted directly using the adaptSkeletonInterpolator method
 * @author Job Zwiers
 */
public class AnimationConverter
{

    private String[] jointSIDs; // used to resolve the GNodes from the skeleton
    private int[] parentIndex; // parentIndex[i] = index of the parent joint of joint i, or -1 if joint i is a root joint.

    private float[][] inverseBindMatrices; // inverse bind matrices, one for every joint, each matrix containing 16 floats, in row major order.
    private float[][] originalInverseBindMatrices; // addition, not essential for main framework, used for adaping skeleton interpolators

    private static Logger logger = LoggerFactory.getLogger(AnimationConverter.class.getName());

    /**
     * Creates a new AnimationConverter for converting animation data (i.e. SkeletonInterpolator data),
     * using the bindpose adaptation, the rotatation, and the HAnim conversion data from a Collada
     * skeleton that is the be found in the Collada file specified as a resource file within
     * a specified resource dir. (The latter can be null or empty,)
     */
    public AnimationConverter(String resourceDir, String resourceFileName)
    {
        readSkeletonAdaptationData(resourceDir, resourceFileName);
    }

    /*
     * Extracts the original as well as current bind matrices, jointSIDs, and parentIndex from
     * the (first) skeleton found in a Collada scene, within the specified resource file, within
     * the specified resource directory. (The latter can be null or empty)
     * mimics SceneIO readGScene, to some extent.
     */
    private void readSkeletonAdaptationData(String resourceDir, String resourceFileName)
    {
        String resDir = (resourceDir == null || resourceDir.equals("")) ? "" : resourceDir.replace('\\', '/') + "/";
        String file = resDir + resourceFileName;
        try
        {
            if (file.endsWith(".dae") || file.endsWith(".DAE"))
            {
                Collada col = Collada.forResource(file);
                if (col == null) throw new RuntimeException("AnimationConverter: null Collada input");
                getSkeletonAdaptationData(col);
            }
            else
            {
                logger.error("AnimationConverter: scene file must be a Collada dae file");
            }
        }
        catch (Exception ioe)
        {
            throw new RuntimeException(ioe.getMessage());
        }
    }

    /*
     * Extracts the original as well as current bind matrices, jointSIDs, and parentIndex from
     * the (first) skeleton found in the Collada scene.
     * This methods mimics the processing from ColladaTranslator methods like
     * colladaToGSkinnedMeshScene and SceneIO meyods lile readGScene.
     */
    private void getSkeletonAdaptationData(Collada collada)
    {
        Scene scene = collada.getScene();
        if (scene == null)
        {
            throw new RuntimeException("AnimationConverter: Collada document without scene.");
        }
        GScene gscene = ColladaTranslator.colladaSceneToGScene(collada, scene);
        // gscene.normalizeMeshes(); // ensure triangle meshes with unified indices: not needed here for animation conversion
        gscene.collectSkinnedMeshes();

        gscene.resolveSkinnedMeshJoints();
        Map<String, String> renaming = new HashMap<String, String>();

        String upAxis = collada.getAsset().getUpAxis();
        if (upAxis.equals("X_UP"))
        {
            gscene.rotate(0f, 0f, 1f, (float) (Math.PI / 2.0)); // from X-up to Y-up
        }
        else if (upAxis.equals("Y_UP") || upAxis.equals(""))
        { // ok, do nothing
        }
        else if (upAxis.equals("Z_UP"))
        {
            gscene.rotate(1f, 0f, 0f, (float) (-Math.PI / 2.0)); // from Z-up to Y-up
        }
        else
        { // unknown up axis.
            logger.error("ColladaTranslator: Collada Asset with unknown or incorrect UP axis: " + upAxis);
        }

        float scale = collada.getAsset().getUnitMeter();
        if (scale != 1.0f) gscene.scale(scale);

        GSkinnedMesh gmesh = gscene.getSkinnedMeshes().get(0);

        inverseBindMatrices = gmesh.getInvBindMatrices(); // before adjustBindPose/HAnim , so original

        originalInverseBindMatrices = new float[inverseBindMatrices.length][Mat4f.MAT4F_SIZE];
        for (int i = 0; i < inverseBindMatrices.length; i++)
        {
            Mat4f.set(originalInverseBindMatrices[i], inverseBindMatrices[i]);
        }

        gmesh.simplifyBindPose();

        String renamingList = collada.getRenamingList();

        if (renamingList != null)
        {
            renaming = ColladaTranslator.getColladaRenaming(renamingList, gscene.getRootNodes());
            gscene.renameJoints(renaming);
        }

        gscene.setSkeletonHAnimPoses();

        this.jointSIDs = gmesh.getJointSIDs();
        this.parentIndex = gmesh.getParentIndex();
        this.inverseBindMatrices = gmesh.getInvBindMatrices();

    }

    /**
     * Adapts the poses from the specified SkeletonInterpolator, using the adaptation data taken from
     * the (first) keleton within the scene specified as argument for the constructor.
     */
    public SkeletonInterpolator adaptSkeletonInterpolator(SkeletonInterpolator skel)
    {
        ConfigList configs = skel.getConfigList();
        int configSize = configs.getConfigSize();
        String configType = skel.getConfigType();
        String[] partIds = skel.getPartIds();
        int nrOfParts = partIds.length;
        float[][] origBindParentMat = new float[nrOfParts][Mat4f.MAT4F_SIZE]; // original bind matrix, from Collada file
        float[][] origInvBindMat = new float[nrOfParts][]; // original inverse bind matrix, from Collada file

        float[][] origBindParentQuat = new float[nrOfParts][Quat4f.QUAT4F_SIZE];
        float[][] origInvBindQuat = new float[nrOfParts][Quat4f.QUAT4F_SIZE];

        float[] Aquat = Quat4f.getQuat4fFromAxisAngleDegrees(1.0f, 0.0f, 0.0f, -90.0f);
        float[] invAquat = Quat4f.getQuat4fFromAxisAngleDegrees(1.0f, 0.0f, 0.0f, 90.0f);

        float[][] bindMat = new float[nrOfParts][Mat4f.MAT4F_SIZE]; // "current" bind matrix, after adjustBindPoses/setHAnim etc
        float[][] invBindParentMat = new float[nrOfParts][Mat4f.MAT4F_SIZE]; // "current" inverse bind mat from parent
        float[][] bindQuat = new float[nrOfParts][Quat4f.QUAT4F_SIZE];
        float[][] invBindParentQuat = new float[nrOfParts][Quat4f.QUAT4F_SIZE];

        for (int pi = 0; pi < nrOfParts; pi++)
        {
            String pid = partIds[pi];
            // search for this part in the skeleton joints
            for (int ji = 0; ji < jointSIDs.length; ji++)
            {
                if (jointSIDs[ji].equals(pid))
                {
                    origInvBindMat[pi] = originalInverseBindMatrices[ji];
                    Quat4f.setFromMat4f(origInvBindQuat[pi], origInvBindMat[pi]);

                    int parIndex = parentIndex[ji];
                    float[] origInvBindParent = null;
                    if (parIndex >= 0)
                    {
                        origInvBindParent = originalInverseBindMatrices[parIndex];
                        invBindParentMat[pi] = inverseBindMatrices[parIndex];
                    }
                    else
                    {
                        origInvBindParent = Mat4f.getIdentity();
                        invBindParentMat[pi] = Mat4f.getIdentity();
                    }
                    Mat4f.invertAffine(origBindParentMat[pi], origInvBindParent);
                    Quat4f.setFromMat4f(origBindParentQuat[pi], origBindParentMat[pi]);

                    Quat4f.setFromMat4f(invBindParentQuat[pi], invBindParentMat[pi]);

                    Mat4f.invertAffine(bindMat[pi], inverseBindMatrices[ji]);
                    Quat4f.setFromMat4f(bindQuat[pi], bindMat[pi]);
                }
            }
        }

        int nrOfJoints = configSize / 4;
        if (nrOfJoints != nrOfParts)
        {
            logger.error("GLSkinnedMesh.adaptSkeletonInterpolator: nrOfParts differs from nrOfJoints");
        }
        int offset = configType.equals("T1R") ? 3 : 0; // assume type is R, or T1R 3 if type is T1R
        int stride = 4;

        float[] q = Quat4f.getQuat4f();
        float[] newq = Quat4f.getQuat4f();

        ConfigList adapted = new ConfigList(configSize);
        for (int ci = 0; ci < configs.size(); ci++)
        {
            float[] cfg = configs.getConfig(ci);
            float[] newcfg = new float[configSize];

            for (int pi = 0; pi < nrOfJoints; pi++)
            {
                q[Quat4f.S] = cfg[offset + stride * pi];
                q[Quat4f.X] = cfg[offset + stride * pi + 1];
                q[Quat4f.Y] = cfg[offset + stride * pi + 2];
                q[Quat4f.Z] = cfg[offset + stride * pi + 3];

                Quat4f.mul(newq, Aquat, q);
                Quat4f.mul(newq, newq, invAquat);
                Quat4f.mul(newq, origBindParentQuat[pi], newq);
                Quat4f.mul(newq, newq, origInvBindQuat[pi]);
                Quat4f.mul(newq, invBindParentQuat[pi], newq);
                Quat4f.mul(newq, newq, bindQuat[pi]);

                newcfg[offset + stride * pi] = newq[Quat4f.S];
                newcfg[offset + stride * pi + 1] = newq[Quat4f.X];
                newcfg[offset + stride * pi + 2] = newq[Quat4f.Y];
                newcfg[offset + stride * pi + 3] = newq[Quat4f.Z];

            }
            adapted.addConfig(configs.getTime(ci), newcfg);
        }
        SkeletonInterpolator result = new SkeletonInterpolator(skel.getPartIds(), adapted, skel.getConfigType());
        return result;
    }

    /**
     * Converts a SkeletonInterpolator, read from file.
     */
    public void convertSkeletonInterpolator(String resourceDir, String skeletonInterpolatorFileName, String convertedInterpolatorFileName)
    {
        String resDir = (resourceDir == null || resourceDir.equals("")) ? "" : resourceDir.replace('\\', '/') + "/";
        String infileName = resDir + skeletonInterpolatorFileName;
        String outfile = resDir + convertedInterpolatorFileName;
        try
        {
            File inFile = new File(infileName);
            XMLTokenizer tk = new XMLTokenizer(inFile);
            SkeletonInterpolator ski = new SkeletonInterpolator(tk);
            // tk.closeReader();
            SkeletonInterpolator adaptedSki = adaptSkeletonInterpolator(ski);
            PrintWriter out = new PrintWriter(outfile);
            int tab = 3;
            adaptedSki.writeXML(out, tab);
            out.close();
        }
        catch (Exception e)
        {
            logger.error("AnimationConvertor: " + e);
        }
    }

    public static void main(String[] arg)
    {

        String humanoidResources = "Humanoids/armandia/dae";
        String colladafile = "armandia-toplevel.dae";
        String infile = "";
        String outfile = "";

        switch (arg.length)
        {
        case 1:
            infile = arg[0];
            outfile = "converted-" + infile;
            break;
        case 2:
            infile = arg[0];
            outfile = arg[1];
            break;
        default:
            System.out.println("provide conversion arguments:  <SkeletonInterpolator file> [<output file>] ");
            System.exit(0);
        }

        logger.info("AnimationConvertor for  " + colladafile + ", converting " + infile + " to " + outfile);

        AnimationConverter converter = new AnimationConverter(humanoidResources, colladafile);
        converter.convertSkeletonInterpolator(null, infile, outfile);
        System.out.println("Conversion finished");
    }

}
