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
import hmi.renderenvironment.HmiRenderEnvironment;
import hmi.renderenvironment.HmiRenderEnvironment.RenderStyle;

/**
 * Provides debug visualisations for VJoints, such as colored spheres, or small
 * colored axis crosses that also indicate the orientation. Use this class as
 * follows: given a VJoint vj that you want to visualize, request to load a debug
 * visualisation for it into the given HmiRenderEnvironment.
 * 
 * Each type of visualisation has recursive and nonrecursive version.
 * 
 */
public final class VJointDebugVisualisations
{
    public enum ShapeType
    {
        SPHERE, BOX, DIAMOND, AXISCROSS
    };

    private VJointDebugVisualisations()
    {
    }

    /**
     * place a small colored shape at the joint.
     * @param size size in centimeters
     * @param type gives type of shape: "sphere" and "box" are centered shapes in given color; "axis-cross" draws an axis cross with red in positive x, green in positive y and
     *            blue in positive z direction from (0,0,0)
     */
    public static VJoint getColoredShapeDebugVisualisation(HmiRenderEnvironment hre, VJoint master, String idPrefix, ShapeType type,
            float[] color, float size)
    {
        VJoint slaveRoot = null;
        String id = idPrefix + "debug-" + master.getId();
        switch (type)
        {
        case SPHERE:
            hre.loadSphere(id, size * 0.005f, 10, 10, RenderStyle.LINE, color, color, color, color);
            break;
        case DIAMOND:
            hre.loadSphere(id, size * 0.005f, 4, 2, RenderStyle.LINE, color, color, color, color);
            break;
        case BOX:
            hre.loadBox(id, new float[] { size * 0.005f, size * 0.005f, size * 0.005f }, RenderStyle.LINE, color, color, color, color);
            break;
        case AXISCROSS:
            hre.loadAxisCross(id, size);
            break;
        }
        slaveRoot = hre.getObjectRootJoint(id);
        slaveRoot.setMaster(master);
        return slaveRoot;
    }

    public static VJoint getColoredShapeDebugVisualisationTree(HmiRenderEnvironment hre, VJoint master, String idPrefix, ShapeType type,
            float[] color, float size)
    {
        VJoint slave = getColoredShapeDebugVisualisation(hre, master, idPrefix, type, color, size);

        for (VJoint child : master.getChildren())
        {
            @SuppressWarnings("unused")
            VJoint childSlave = getColoredShapeDebugVisualisationTree(hre, child, idPrefix, type, color, size);
            hre.setObjectParent(idPrefix + "debug-" + child.getId(), idPrefix + "debug-" + master.getId());
        }

        return slave;
    }
}
