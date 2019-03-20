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
