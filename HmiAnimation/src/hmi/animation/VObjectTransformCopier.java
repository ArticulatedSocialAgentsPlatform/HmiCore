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
/*  
 * 
 */

package hmi.animation;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copies transformations from a list of source VObject to a list of destination VObjects 
 * @author welberge
 */
public class VObjectTransformCopier
{

    private final VObject[] srcParts;

    private final VObject[] dstParts;

    private String configType;

    private boolean hasRootTranslation, hasTranslation, hasRotation, hasScale, hasVelocity,
            hasAngularVelocity;

    private float[] buf = new float[4]; // local buffer for copying Vec3f and Quat4f values.

	private int rootTranslationIdx = 0;

    private static final VObject[] emptyParts = new VObject[0];
     private static Logger logger = LoggerFactory.getLogger(VObjectTransformCopier.class.getName());

    protected VObjectTransformCopier(VObject[] srcParts,VObject[] dstParts,String type)
    {
        this.srcParts = srcParts;
        this.dstParts = dstParts;
        setConfigType(type);
    }
    
    /**
     * Creates a new VObject copier from lists of VObjects WITHOUT CHECKING, assuming that the one
     * who calls the constructor makes sure that src and dst match 
     */
    public static <T extends VObject> VObjectTransformCopier  newInstanceFromMatchingVObjectLists(List<T> srcList, List<T> dstList,String type)
    {
        ArrayList<VObject> selectedSrcList = new ArrayList<VObject>(srcList.size());
        ArrayList<VObject> selectedDstList = new ArrayList<VObject>(srcList.size());
        for (int i = 0; i < srcList.size(); i++)
        {
            selectedSrcList.add(srcList.get(i));
            selectedDstList.add(dstList.get(i));
        }
        VObject[] srcParts = selectedSrcList.toArray(emptyParts);
        VObject[] dstParts = selectedDstList.toArray(emptyParts);
        
        return new VObjectTransformCopier(srcParts,dstParts,type);
    }

    /**
     * Creates a new VObject copier from lists of VObjects
     */
    public static <T extends VObject> VObjectTransformCopier newInstanceFromVObjectLists(List<T> srcList, List<T> dstList, String type)
    {
        ArrayList<VObject> selectedSrcList = new ArrayList<VObject>(srcList.size());
        ArrayList<VObject> selectedDstList = new ArrayList<VObject>(srcList.size());
        for (VObject srcObject : srcList)
        {
            for (VObject dstObject : dstList)
            {
                if (equiv(srcObject, dstObject))
                {
                    selectedSrcList.add(srcObject);
                    selectedDstList.add(dstObject);
                    break;
                }
            }
        }
        VObject[] srcParts = selectedSrcList.toArray(emptyParts);
        VObject[] dstParts = selectedDstList.toArray(emptyParts);
        return new VObjectTransformCopier(srcParts,dstParts,type);
    }

    /**
     * Creates a new VObject copier from two VJoint root objects
     */
    public static VObjectTransformCopier newInstanceFromVJointTree(VJoint sourceRoot, VJoint destinationRoot, String type)
    {
        return newInstanceFromVObjectLists(sourceRoot.getParts(), destinationRoot.getParts(), type);
    }

    /* checks for equivalent ids, sids or names */
    private static boolean equiv(VObject src, VObject dst)
    {
        return ((src.getId() != null && src.getId().equals(dst.getId()))
                || (src.getSid() != null && src.getSid().equals(dst.getSid())) 
                || (src.getName() != null && src.getName().equals(dst.getName())));
    }
    

    public void setRootTranslationIdx(int rootTranslationIdx)
    {
    	this.rootTranslationIdx  = rootTranslationIdx;
    }

    public void setConfigType(String configType)
    {
        this.configType = configType;
        hasRootTranslation = configType.startsWith("T1");
        if (!hasRootTranslation) hasTranslation = (configType.indexOf('T') >= 0);
        hasRotation = configType.indexOf('R') >= 0;
        hasScale = configType.indexOf('S') >= 0;
        hasVelocity = configType.indexOf('V') >= 0;
        hasAngularVelocity = configType.indexOf('W') >= 0;
    }

    /**
     * Copies translation, rotation and,
     */
    public void copyConfig()
    {
        if (srcParts == null || dstParts == null)
        {
            logger.error("VObjectTreeCopier.copyConfig: null source or destination");
            return;
        }
        if (hasRootTranslation)
        {
            srcParts[rootTranslationIdx].getTranslation(buf);
            dstParts[rootTranslationIdx].setTranslation(buf);
        }
        if (hasTranslation)
        {
            for (int i = 0; i < srcParts.length; i++)
            {
                srcParts[i].getTranslation(buf);
                dstParts[i].setTranslation(buf);
            }
        }
        if (hasRotation)
        {
            for (int i = 0; i < srcParts.length; i++)
            {
                srcParts[i].getRotation(buf);
                dstParts[i].setRotation(buf);
            }
        }
        if (hasScale)
        {
            for (int i = 0; i < srcParts.length; i++)
            {
                srcParts[i].getScale(buf);
                dstParts[i].setScale(buf);
            }
        }
        if (hasVelocity)
        {
            for (int i = 0; i < srcParts.length; i++)
            {
                srcParts[i].getVelocity(buf);
                dstParts[i].setVelocity(buf);
            }
        }
        if (hasAngularVelocity)
        {
            for (int i = 0; i < srcParts.length; i++)
            {
                srcParts[i].getAngularVelocity(buf);
                dstParts[i].setAngularVelocity(buf);
            }
        }

    }

    public String getConfigType()
    {
        return configType;
    }
}
