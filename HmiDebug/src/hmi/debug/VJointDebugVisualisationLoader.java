/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
package hmi.debug;

import hmi.animation.VJoint;
import hmi.animationembodiments.SkeletonEmbodiment;
import hmi.debug.VJointDebugVisualisations.ShapeType;
import hmi.environmentbase.EmbodimentLoader;
import hmi.environmentbase.Environment;
import hmi.environmentbase.Loader;
import hmi.math.Quat4f;
import hmi.renderenvironment.HmiRenderEnvironment;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VJointDebugVisualisationLoader implements Loader
{

    // some parameters for caching during XML loading

    private XMLStructureAdapter adapter = new XMLStructureAdapter();
    private HmiRenderEnvironment hre = null;
    private SkeletonEmbodiment se = null;

    private String debugId = null;
    private ShapeType shapeType = ShapeType.SPHERE;

    @Getter
    @Setter
    private String id = "";
    private String vhId = "";

    private float[] color = new float[] { 0.4f, 0.4f, 0.7f, 1 };

    @Override
    public void unload()
    {
        log.debug("Removing debug visualisation for skeleton");
        hre.unloadObject(debugId);
    }

    @Override
    public void readXML(XMLTokenizer tokenizer, String loaderId, String vhId, String vhName, Environment[] environments, Loader... requiredLoaders)
            throws IOException
    {
        this.id = loaderId;
        this.vhId = vhId;

        for (Environment e : environments)
        {
            if (e instanceof HmiRenderEnvironment) hre = (HmiRenderEnvironment) e;
        }
        for (Loader e : requiredLoaders)
        {
            if (e instanceof EmbodimentLoader && ((EmbodimentLoader) e).getEmbodiment() instanceof SkeletonEmbodiment)
            {
                se = (SkeletonEmbodiment) ((EmbodimentLoader) e).getEmbodiment();
            }
        }
        if (se == null)
        {
            throw new RuntimeException("VJointDebugVisualisationLoader requires an Embodiment of type SkeletonEmbodiment");
        }
        if (hre == null)
        {
            throw new RuntimeException("VJointDebugVisualisationLoader requires an HmiRenderEnvironment");
        }

        float[] debugTranslation = new float[] { 0, 0, 0 };
        float[] debugOrientation = new float[] { 0, 0, 0, 0 };

        if (tokenizer.atSTag("Placement"))
        {
            HashMap<String, String> attrMap = tokenizer.getAttributes();
            String offsetString = adapter.getOptionalAttribute("offset", attrMap, "0 0 0");
            debugTranslation = XMLStructureAdapter.decodeFloatArray(offsetString);
            if (debugTranslation.length != 3) throw tokenizer.getXMLScanException("Placement.offset must containg a 3-float array");
            String rotString = adapter.getOptionalAttribute("rotation", attrMap, "0 0 0 0");
            debugOrientation = XMLStructureAdapter.decodeFloatArray(rotString);
            if (debugOrientation.length != 4) throw tokenizer.getXMLScanException("Placement.rotation must containg a 4-float array");
            String typeString = adapter.getOptionalAttribute("type", attrMap, "diamond").toLowerCase();
            if (typeString.equals("diamond"))
            {
                shapeType = ShapeType.DIAMOND;
            }
            else if (typeString.equals("box"))
            {
                shapeType = ShapeType.BOX;
            }
            else if (typeString.equals("axis-cross"))
            {
                shapeType = ShapeType.AXISCROSS;
            }
            tokenizer.takeSTag("Placement");
            tokenizer.takeETag("Placement");
        }
        VJointDebugVisualisations.getColoredShapeDebugVisualisationTree(hre, se.getAnimationVJoint(), vhId + "_" + loaderId
                + "_", shapeType, color, 2);
        VJoint debugJoint = hre
                .insertParentJointForObject(vhId + "_" + loaderId + "_debug-" + se.getAnimationVJoint().getId());
        VJoint parent = hre.insertParentJointForObject(debugJoint.getId());
        debugId = parent.getId();
        parent.setTranslation(debugTranslation);
        float[] qRot = new float[4];
        Quat4f.setFromAxisAngle4f(qRot, debugOrientation);
        parent.rotate(qRot);

    }

}
