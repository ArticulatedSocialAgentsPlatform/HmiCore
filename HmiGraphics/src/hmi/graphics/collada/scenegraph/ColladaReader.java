/*******************************************************************************
 * Copyright (C) 2009-2020 Human Media Interaction, University of Twente, the Netherlands
 *
 * This file is part of the Articulated Social Agents Platform BML realizer (ASAPRealizer).
 *
 * ASAPRealizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ASAPRealizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ASAPRealizer.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
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
