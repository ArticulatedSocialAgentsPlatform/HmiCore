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

package hmi.graphics.collada.scenegraph;

import hmi.graphics.collada.Collada;
import hmi.graphics.scenegraph.GScene;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Collada IO for graphics. 
 * 
 * @author Job Zwiers
 */
public final class ColladaReader
{

    /* Disable creation of ColladaReader objects  */
    private ColladaReader()  { }
    
    /**
     * Reads a Collada scene from the specified Collada file, within the specified resource directory.
     * A joint renaming is applied, as specified by the jointRenaming file. 
     * The scene is scaled by the specified scale factor.
     */
    public static GScene readGSkinnedMeshScene(String colladaResourceDir, String colladaFileName, boolean setToHanim) 
    throws IOException
    {
        String resDir = (colladaResourceDir == null || colladaResourceDir.equals("")) ? "" :  colladaResourceDir.replace('\\', '/') + "/";
        String colladaFile = resDir + colladaFileName;
       // String renamingFile = (jointRenamingFileName == null || jointRenamingFileName.equals("")) ? null : resDir + jointRenamingFileName;
        return readGSkinnedMeshScene(colladaFile, setToHanim);
    }


    /**
     * Reads a Collada scene from the specified Collada file, within the specified resource directory.
     * A joint renaming is applied, as specified by the jointRenaming file. 
     * The scene is scaled by the specified scale factor.
     */
    public static GScene readGSkinnedMeshScene(String colladaFile, boolean setToHAnim) throws IOException
    {
        Collada col = Collada.forResource(colladaFile);
       // String renaming = (renamingFile==null || renamingFile.equals("")) ? "" : Resources.readResource(renamingFile);
        GScene gscene = ColladaTranslator.colladaToGSkinnedMeshScene(col);
        logger.info("===ColladaReader.readGSkinnedMeshScene");
        if (setToHAnim) {
            gscene.setSkeletonHAnimPoses(); 
            gscene.calculateVJointMatrices(); 
        }
        return gscene;
    }

 
 

 

    public static GScene readGSkinnedMeshSceneBinary(String sceneFile) throws IOException
    {
        String resourceRoot = "";
        InputStream inps = hmi.graphics.collada.scenegraph.ColladaReader.class.getClassLoader().getResourceAsStream(resourceRoot+sceneFile);
        DataInputStream dataIn = new DataInputStream(inps);
        GScene gscene = new GScene("");
        gscene.readBinary(dataIn);
        dataIn.close();

        gscene.collectSkinnedMeshes();
        gscene.resolveSkinnedMeshJoints(); // resolve skeleton roots and skeleton joints
        return gscene;
    }


  


    
        private static Logger logger = LoggerFactory.getLogger("hmi.graphics.scenegraph");


}
