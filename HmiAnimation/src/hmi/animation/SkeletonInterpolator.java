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
package hmi.animation;

import hmi.math.Quat4f;
import hmi.math.Vec3f;
import hmi.util.ClockListener;
import hmi.util.Resources;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A SkeletonInterpolator is an interpolator for simultaneous animation of several selected parts of
 * a VObject tree. It is assumed that the joints of this skeleton tree have rotations that are to be
 * interpolated. Optionally, the root joint has also a translation that must be interpolated.
 * Scaling, if any, or translation of joints other than the root are considered to be fixed.
 */
public class SkeletonInterpolator extends XMLStructureAdapter implements ClockListener
{
    public static final String ROOT_TRANSFORM = "T1";
    public static final String ROTATION = "R";
    public static final String TRANSLATION = "T";

    private static Logger logger = LoggerFactory.getLogger(SkeletonInterpolator.class.getName());

    private static final String[] empty_PartIds = new String[0];

    private String[] partIds = empty_PartIds;

    private ConfigList configs;

    private String configType;

    private int configSize; // length of a single config, in number of floats.

    private int stride; // number of floats for a single joint, except for a possible
                        // rootTranslation

    private boolean hasRootTranslation, hasTranslation, hasRotation, hasScale, hasVelocity, hasAngularVelocity;

    private String rotationEncoding = "Quat"; // default is quaternions

    private VObject[] targetParts;

    private VJoint target;

    // cached values for the interpolation process:
    private int lowerIndex, upperIndex;

    private double lowerTime, upperTime, interval;

    private float[] lowerConfig, upperConfig;

    private float[] buf = new float[4]; // temp copy buffer for Vec3f and Quat4f elements

    /**
     * Creates a new, uninitialized, SkeletonInterpolator
     */
    public SkeletonInterpolator(XMLTokenizer tokenizer) throws IOException
    {
        super();
        readXML(tokenizer);
    }

    /**
     * Creates a new SkeletonInterpolator for a specified ConfigList, VParts, and Config type. The
     * Config type should be a String like "T1R" or "R", or "TRSVW".
     */
    public SkeletonInterpolator(String[] partIds, ConfigList configs, String configType)
    {
        super();
        setPartIds(partIds);
        setConfigList(configs);
        setConfigType(configType);
        calculateConfigSize();
    }

    public SkeletonInterpolator(SkeletonInterpolator p, VObject[] targetParts)
    {
        super();
        setPartIds(p.partIds.clone());
        setConfigList(p.configs.copy());
        setConfigType(p.configType);
        stride = p.stride;
        hasRootTranslation = p.hasRootTranslation;
        hasTranslation = p.hasTranslation;
        hasRotation = p.hasRotation;
        hasScale = p.hasScale;
        hasVelocity = p.hasVelocity;
        hasAngularVelocity = p.hasAngularVelocity;
        rotationEncoding = p.rotationEncoding;
        target = p.target;
        this.targetParts = targetParts;
    }

    public SkeletonInterpolator(SkeletonInterpolator p)
    {
        super();
        setPartIds(p.partIds.clone());
        setConfigList(p.configs.copy());
        setConfigType(p.configType);
        stride = p.stride;
        hasRootTranslation = p.hasRootTranslation;
        hasTranslation = p.hasTranslation;
        hasRotation = p.hasRotation;
        hasScale = p.hasScale;
        hasVelocity = p.hasVelocity;
        hasAngularVelocity = p.hasAngularVelocity;
        rotationEncoding = p.rotationEncoding;
        targetParts = p.targetParts;
        target = p.target;
    }

    public SkeletonInterpolator()
    {
        configs = new ConfigList(0);
    }

    /**
     * Sets the ConfigList, defining the VPartsConfigs to be interpolated.
     */
    public void setConfigList(ConfigList configs)
    {
        this.configs = configs;
        if (configs != null && configs.size() != 0)
        {
            lowerIndex = upperIndex = 0;
            lowerTime = upperTime = configs.getTime(0);
            interval = 0.0;
        }
    }

    /**
     * Gets the Config list
     */
    public ConfigList getConfigList()
    {
        return configs;
    }

    /**
     * Sets the list of VObject ids/sids
     */
    public void setPartIds(String[] partIds)
    {
        this.partIds = partIds;        
        calculateConfigSize();
    }

    /**
     * Returns the list of ids of the VObject parts that are influenced by this interpolator.
     */
    public String[] getPartIds()
    {
        return partIds;
    }

    /**
     * Returns the configuration type, as encoded conform VOBject types.
     */
    public String getConfigType()
    {
        return configType;
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
        calculateConfigSize();
    }

    private void calculateConfigSize()
    {
        stride = 0;
        if (hasTranslation) stride += 3;
        if (hasRotation) stride += 4;
        if (hasScale) stride += 3;
        if (hasVelocity) stride += 3;
        if (hasAngularVelocity) stride += 3;
        configSize = stride * partIds.length;
        if (hasRootTranslation) configSize += 3;
    }

    public void setRotationEncoding(String rotationEncoding)
    {
        this.rotationEncoding = rotationEncoding;
    }

    /**
     * Returns the (uniform) size of configs, in number of floats.
     */
    public int getConfigSize()
    {
        return (configs == null) ? 0 : configs.getConfigSize();
    }

    /**
     * returns the size of the list
     */
    public int size()
    {
        return (configs == null) ? 0 : configs.size();
    }

    /**
     * Returns the time for config i
     */
    public double getTime(int i)
    {
        return (configs == null) ? 0.0 : configs.getTime(i);
    }

    /**
     * Returns the Config at index i
     */
    public float[] getConfig(int i)
    {
        return (configs == null) ? null : configs.getConfig(i);
    }

    public double getStartTime()
    {
        return (configs == null) ? 0.0 : configs.getStartTime();
    }

    public double getEndTime()
    {
        return (configs == null) ? 0.0 : configs.getEndTime();
    }

    /**
     * Sets a specified VJoint as target for interpolation A lookup is performed for parts of the
     * target with Id/Sid/Name as defined by the partIds for this interpolator.
     */
    public void setTarget(VJoint target)
    {
        this.target = target;
        if (target == null)
        {
            targetParts = null;
        }
        else
        {
            if (targetParts == null) targetParts = new VJoint[partIds.length];
            for (int i = 0; i < partIds.length; i++)
            {
                targetParts[i] = target.getPart(partIds[i]);
            }
        }
    }

    // get the number of floats used to the describe 1 config of partIndex
    private int getWidth(int partIndex)
    {
        int width = 0;
        if (hasRootTranslation && partIndex == 0)
        {
            width += 3;
        }
        else if (hasTranslation)
        {
            width += 3;
        }
        if (hasRotation)
        {
            width += 4;
        }
        if (hasScale)
        {
            width += 3;
        }
        if (hasVelocity)
        {
            width += 3;
        }
        if (hasAngularVelocity)
        {
            width += 3;
        }
        return width;
    }

    private void filterTargetParts(Set<String> joints)
    {
        ArrayList<VObject> newParts = new ArrayList<VObject>();
        int i = 0;
        for (VObject vj : targetParts)
        {
            if (joints.contains(partIds[i]))
            {
                newParts.add(vj);
            }
            i++;
        }
        targetParts = newParts.toArray(new VObject[0]);
    }

    /**
     * Filter out all parts that are not in joints
     */
    public void filterJoints(Set<String> joints)
    {
        int index = 0;
        ArrayList<String> newPartIds = new ArrayList<String>();
        int configSize = 0;
        boolean removeRoot = false;
        for (int i = 0; i < partIds.length; i++)
        {
            if (joints.contains(partIds[i]))
            {
                newPartIds.add(partIds[i]);
                configSize += getWidth(i);
            }
            else if (i == 0)
            {
                removeRoot = true;

            }
            index += getWidth(i);
        }
        ConfigList newConfig = new ConfigList(configSize);

        for (int j = 0; j < configs.size(); j++)
        {
            float src[] = configs.getConfig(j);
            float dst[] = new float[configSize];
            int newConfigIndex = 0;
            index = 0;
            for (int i = 0; i < partIds.length; i++)
            {
                if (joints.contains(partIds[i]))
                {
                    System.arraycopy(src, index, dst, newConfigIndex, getWidth(i));
                    newConfigIndex += getWidth(i);
                }
                index += getWidth(i);
            }
            newConfig.addConfig(configs.getTime(j), dst);
        }
        if (targetParts != null)
        {
            filterTargetParts(joints);
        }
        setPartIds(newPartIds.toArray(new String[0]));
        setConfigList(newConfig);
        if (removeRoot)
        {
            hasRootTranslation = false;
            if (configType.startsWith("T1"))
            {
                configType = configType.substring(2);
            }
        }
    }

    /**
     * calculates the current config for the specified time in milliseconds and copies it to the
     * current target
     */
    public void interpolateMillis(long time)
    {
        // System.out.println("interpolate(long: " + time + ")");
        interpolateTargetParts(time / 1000.0);
    }

    /**
     * Returns the interpolated config array for the specified time t, in the specified conf float
     * array. If the latter is null, a new float array is allocated.
     */
    public float[] getInterpolatedConfig(double t, float[] conf)
    {
        if (configs.size() == 0) return null;
        if (conf == null) conf = new float[configs.getConfig(0).length];
        float alpha = getInterpolationConfigs(t); // sets lowerConfig and upperConfig
        interpolateConfigs(conf, alpha, lowerConfig, upperConfig);
        return conf;
    }

    private void interpolateConfigs(float[] conf, float alpha, float[] lowerConfig, float[] upperConfig)
    {
        int index = 0;
        if (hasRootTranslation && partIds.length > 0)
        {
            Vec3f.interpolate(conf, 0, lowerConfig, 0, upperConfig, 0, alpha);
            index += 3;
        }
        for (int i = 0; i < partIds.length; i++)
        {
            if (hasTranslation)
            {
                Vec3f.interpolate(conf, index, lowerConfig, index, upperConfig, index, alpha);
                index += 3;
            }
            if (hasRotation)
            {
                Quat4f.interpolate(conf, index, lowerConfig, index, upperConfig, index, alpha);
                index += 4;
            }
            if (hasScale)
            {
                Vec3f.interpolate(conf, index, lowerConfig, index, upperConfig, index, alpha);
                index += 3;
            }
            if (hasVelocity)
            {
                Vec3f.interpolate(conf, index, lowerConfig, index, upperConfig, index, alpha);
                index += 3;
            }
            if (hasAngularVelocity)
            {
                Vec3f.interpolate(conf, index, lowerConfig, index, upperConfig, index, alpha);
                index += 3;
            }
        }
    }

    /**
     * The time method for the ClockListener interface; equivalent to interpolateTargetParts
     */
    public void initTime(double t)
    {
    }

    /**
     * The time method for the ClockListener interface; equivalent to interpolateTargetParts
     */
    public void time(double t)
    {
        interpolateTargetParts(t);
    }

    /**
     * Set targetparts to the configlist at frame c
     */
    public void setTargetParts(int c)
    {
        float config[] = getConfig(c);
        int index = 0;
        if (hasRootTranslation)
        {
            targetParts[0].setTranslation(config, index);
            index += 3;
        }
        for (int i = 0; i < targetParts.length; i++)
        {
            if (hasTranslation)
            {
                targetParts[i].setTranslation(config, index);
                index += 3;
            }
            if (hasRotation)
            {
                targetParts[i].setRotation(config, index);
                index += 4;
            }
            if (hasScale)
            {
                targetParts[i].setScale(config, index);
                index += 3;
            }
        }
    }
    
    /**
     * start = true if the given values are meant for the first frame of the SkeletonInterpolator, false if for the final frame
     */
    public void setHumanoidRootTranslation(float[] translation, boolean start) {
    	float[] refFrame = start ? this.getConfig(0) : this.getConfig(this.size()-1); 
    	
    	float[] refTranslation = new float[3];
    	Vec3f.set(refTranslation, refFrame[Vec3f.X], refFrame[Vec3f.Y], refFrame[Vec3f.Z]);
    	Vec3f.sub(translation, refTranslation);
    	ConfigList newConfig = new ConfigList(this.getConfigSize());
    	for (int i = 0; i < this.getConfigList().size(); i++) {
            float[] config = this.getConfig(i);
            
            double time = this.getTime(i);
            config[Vec3f.X] = config[Vec3f.X] + translation[Vec3f.X];
            //config[Vec3f.Y] = config[Vec3f.Y] + translation[Vec3f.Y];
            config[Vec3f.Z] = config[Vec3f.Z] + translation[Vec3f.Z];

            newConfig.addConfig(time, config);
        }

        this.setConfigList(newConfig);            
    }

    /**
     * start = true if the given values are meant for the first frame of the SkeletonInterpolator, false if for the final frame
     */
    public void setHumanoidRootRotation(float[] rotation, boolean start) {
    	float[] refFrame = start ? this.getConfig(0) : this.getConfig(this.size()-1);
    	
    	float[] refRotation = new float[4];
    	Quat4f.set(refRotation, refFrame[Quat4f.S + 3], refFrame[Quat4f.X + 3], refFrame[Quat4f.Y + 3], refFrame[Quat4f.Z + 3]);
    	
    	float[] inverseRotation = Quat4f.getQuat4f(refRotation);
    	Quat4f.inverse(inverseRotation);
    	
    	float[] applyingRotation = new float[4];
    	Quat4f.mul(applyingRotation, rotation, inverseRotation);

    	ConfigList newConfig = new ConfigList(this.getConfigSize());
    	for (int i = 0; i < this.getConfigList().size(); i++) {
            float[] config = this.getConfig(i);
            
            double time = this.getTime(i);
            
            float[] frameRotation = new float[4];
            Quat4f.set(frameRotation, config[Quat4f.S + 3], config[Quat4f.X + 3], config[Quat4f.Y + 3], config[Quat4f.Z + 3]);
            
            float[] newRotation = Quat4f.getQuat4f();
            Quat4f.mul(newRotation, rotation, frameRotation);
            Quat4f.set(config, 3, newRotation, 0);
            
            Quat4f.transformVec3f(rotation, config);
            
            newConfig.addConfig(time, config);
        }

        this.setConfigList(newConfig);            
    }
    
    public void mirrorYAxis() {
    	ConfigList newConfig = new ConfigList(this.getConfigSize());
    	for (int i = 0; i < this.getConfigList().size(); i++) {
            float[] config = this.getConfig(i);
            
            double time = this.getTime(i);
            
            config[Quat4f.X+3] = -1 * config[Quat4f.X+3]; 
            config[Quat4f.Z+3] = -1 * config[Quat4f.Z+3]; 
            		
            newConfig.addConfig(time, config);
        }

        this.setConfigList(newConfig);            
    	
    }
    
    /**
     * Interpolates two float array configurations lowerConfig and upperConfig, and places the
     * result in the target VObjects. Only translation, rotation, and scale can be interpolated. The
     * type parameter determines the types of the configuration, and how to interpolate individual
     * elements of the configurations, like lerp, quaternion slerp. It is assumed that all arrays
     * are allocated, i.e. non-null, and have the appropriate length.
     */
    public void interpolateTargetParts(double time)
    {
        if (targetParts == null) return;
        if (configs.size() == 0) return;
        float alpha = getInterpolationConfigs(time); // sets lowerConfig and upperConfig
        int index = 0;
        if (hasRootTranslation)
        {
            Vec3f.interpolate(buf, 0, lowerConfig, 0, upperConfig, 0, alpha);
            targetParts[0].setTranslation(buf);
            index += 3;
        }
        for (int i = 0; i < targetParts.length; i++)
        {
            // can happen if the SkeletonInterpolator contains joints that are not in the target
            if (targetParts[i] == null) continue;

            if (hasTranslation)
            {
                Vec3f.interpolate(buf, 0, lowerConfig, index, upperConfig, index, alpha);
                targetParts[i].setTranslation(buf);
                index += 3;
            }
            if (hasRotation)
            {
                Quat4f.interpolate(buf, 0, lowerConfig, index, upperConfig, index, alpha);
                targetParts[i].setRotation(buf);
                index += 4;
            }
            if (hasScale)
            {
                Vec3f.interpolate(buf, 0, lowerConfig, index, upperConfig, index, alpha);
                targetParts[i].setScale(buf);
                index += 3;
            }
        }
    }

    /**
     * Finds the interval [lowerIndex, upperIndex] for a specified time t, such that
     * time[lowerIndex] <= t < time[upperIndex], where upperIndex == lowerIndex+1. This assumes that
     * 1) the list is non-empty, 2) time[0] <= t < time[listSize-1] 3) previous values of lowerIndex
     * and upperIndex are such that lowerIndex==upperIndex or lowerIndex+1==upperIndex. In this
     * case, true is returned. Special case 1: when size() == 0, false is returned and lowerIndex ==
     * upperIndex ==0. Special case 2): when t < time[0], false is returned and lowerIndex ==
     * upperIndex ==0 Special case 3) : when t>= time[listSize-1], false is returned and lowerIndex
     * == upperIndex == listSize-1
     * 
     * or when t < time[0], then the interval is [0, 0], i.e lowerIndex==upperIndex==0 On the other
     * hand, when time[listSize-1] <= t then the interval is defined to be [listSize-1, listSize-1]
     * The two indices are always legal indices, i.e. inside the list range, except when the list is
     * empty. Upon return the following fields are set: lowerIndex, upperIndex lowerTime, upperTime
     * lowerConfig, UpperConfig The interpolation weight factor alpha is returned. Example: for a
     * list with listSize==5, and with time stamps as follows: [100, 200, 200, 200, 300], we have:
     * findInterpolateInterval(0) = findInterpolateInterval(99) = [0, 0]
     * findInterpolateInterval(100) = findInterpolateInterval(150) = [0, 1]
     * findInterpolateInterval(200) = findInterpolateInterval(250) = [3, 4]
     * findInterpolateInterval(300) = findInterpolateInterval(500) = [4, 4]
     */
    private float getInterpolationConfigs(double t)
    {
        /*
         * Invariant: lowerTime = configs.getTime(lowerIndex), upperTime =
         * configs.getTime(upperIndex) lowerConfig = configs.getConfig(lowerIndex), upperConfig =
         * configs.getConfig(upperIndex); interval = upperTime - lowerTime;
         */
        if (lowerTime <= t && t < upperTime)
        { // check for "fast path" conditions
          // lowerTime, upperTime, lowerIndex, upperIndex, lowerConfig, upperConfig, interval
          // unchanged.
            return (float) ((t - lowerTime) / interval);
        }
        if (t < lowerTime)
        {
            if (t < configs.getStartTime())
            {
                lowerIndex = upperIndex = 0; // no search needed
            }
            else
            {
                upperIndex = lowerIndex; // t < time[upperIndex]
                lowerIndex = 0; // time[lowerIndex] <= t < time[upperIndex]
            }
        }
        else
        { // time[upperIndex] <= t
            if (t >= configs.getEndTime())
            {
                lowerIndex = upperIndex = configs.size() - 1; // time >= time[listSize-1], so
                                                              // interval [listSize-1, listSize-1]
            }
            else
            {
                lowerIndex = upperIndex; // time[lowerIndex] <= t
                if (t < configs.getTime(lowerIndex + 1))
                { // test for common special case, just for speedup
                    upperIndex = lowerIndex + 1;
                }
                else
                {
                    upperIndex = configs.size() - 1; // time[lowerIndex] <= t < time[upperIndex]
                }
            }
        }
        // invariant: time[lowerIndex] <= t < time[upperIndex] && lowerIndex < upperIndex
        while (upperIndex - lowerIndex > 1)
        { // iterate until upperIndex == lowerIndex+1
            int probe = (upperIndex + lowerIndex) / 2; // lowerIndex < probe < upperIndex
            if (t < configs.getTime(probe))
            {
                upperIndex = probe;
            }
            else
            {
                lowerIndex = probe;
            }
        } // time[lowerIndex] <= t && t <time[upperIndex] && upperIndex = lowerIndex+1
        if (upperIndex != lowerIndex + 1 && upperIndex != lowerIndex) logger.debug("***********************************");
        if (upperIndex != lowerIndex + 1 && upperIndex != lowerIndex)
        {

            logger.debug("lowerindex: {}", lowerIndex);
            logger.debug("upperindex: {}", upperIndex);
            logger.debug("-------------------");
        }

        lowerTime = configs.getTime(lowerIndex);
        upperTime = configs.getTime(upperIndex);

        lowerConfig = configs.getConfig(lowerIndex);
        upperConfig = configs.getConfig(upperIndex);

        interval = upperTime - lowerTime;
        float alpha = (interval <= 0.0f) ? 0.0f : (float) ((t - lowerTime) / interval);
        return alpha;
    }

    /**
     * Samples the transformations from the target VObjects, and stores these as configuration on
     * the specified time.
     */
    public void sampleTargetParts(double time)
    {
        if (targetParts == null) return;
        
        float[] newConfig = new float[configSize];
        int index = 0;
        if (hasRootTranslation)
        {
            targetParts[0].getTranslation(newConfig, index);
            index += 3;
        }
        for (int i = 0; i < targetParts.length; i++)
        {
            if (hasTranslation)
            {
                targetParts[i].getTranslation(newConfig, index);
                index += 3;
            }
            if (hasRotation)
            {
                targetParts[i].getRotation(newConfig, index);
                index += 4;
            }
            if (hasScale)
            {
                targetParts[i].getScale(newConfig, index);
                index += 3;
            }
        }
        configs.addConfig(time, newConfig);
    }

    private void mirrorParts(int i)
    {
        if (targetParts != null && targetParts.length > 0)
        {
            if (!targetParts[i].getSid().equals(partIds[i]))
            {
                for (int j = 0; j < targetParts.length; j++)
                {
                    if (targetParts[j].getSid().equals(partIds[i]))
                    {
                        VObject temp = targetParts[i];
                        targetParts[i] = targetParts[j];
                        targetParts[j] = temp;
                        return;
                    }
                }
                targetParts[i] = target.getPart(partIds[i]);
            }
        }
    }

    /**
     * Mirrors all joint rotations on the XY plane, switches left/right partIds
     */
    public void mirror()
    {
        int index = 0;
        if (hasRootTranslation)
        {
            configs.mirrorTranslation(index);
            index += 3;
        }
        for (int i = 0; i < partIds.length; i++)
        {
            if (partIds[i].startsWith("l_"))
            {
                partIds[i] = partIds[i].replace("l_", "r_");
                mirrorParts(i);
            }
            else if (partIds[i].startsWith("r_"))
            {
                partIds[i] = partIds[i].replace("r_", "l_");
                mirrorParts(i);
            }

            if (hasTranslation)
            {
                configs.mirrorTranslation(index);
                index += 3;
            }
            if (hasRotation)
            {
                logger.debug("mirroring {}, index {}", partIds[i], index);
                configs.mirrorRotation(index);
                index += 4;
            }
            if (hasScale)
            {
                index += 3;
            }
            if (hasVelocity)
            {
                index += 3;
            }
            if (hasAngularVelocity)
            {
                index += 3;
            }
        }

    }

    /**
     * Appends a String of signature attributes to buf
     */
    @Override
    public StringBuilder appendAttributeString(StringBuilder buf, XMLFormatting fmt)
    {
        appendAttribute(buf, "encoding", configType);
        if (rotationEncoding != null) appendAttribute(buf, "rotationEncoding", rotationEncoding);
        if (partIds != null && partIds.length > 0)
        {
            appendAttribute(buf, "parts", partIds, ' ', fmt, 10);
        }
        return buf;
    }

    /**
     * decodes the XML attributes
     */
    @Override
    public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer)
    {
        String parts = getRequiredAttribute("parts", attrMap, tokenizer);
        partIds = decodeStringArray(parts);
        String encoding = getRequiredAttribute("encoding", attrMap, tokenizer);
        setConfigType(encoding);
        rotationEncoding = getOptionalAttribute("rotationEncoding", attrMap, "Quat");
        super.decodeAttributes(attrMap, tokenizer);
    }

    /**
     * Appends the config elements as XML content.
     */
    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt)
    {
        configs.appendContent(buf, fmt);
        return buf;
    }

    /**
     * Decodes XML content, and converts it into the double time values and float cofig data.
     */
    @Override
    public void decodeContent(XMLTokenizer xmlTokenizer) throws IOException
    {
        calculateConfigSize();
        configs = new ConfigList(configSize);
        configs.decodeContent(xmlTokenizer);
        if (rotationEncoding != null)
        {
            if (rotationEncoding.equals("axisangles") || rotationEncoding.equals("Axisangles") || rotationEncoding.equals("AxisAngles"))
            {
                convertFromAxisAngles();
            }
            else if (rotationEncoding.equals("quat") || rotationEncoding.equals("Quat"))
            {// do nothing
            }
            else if (rotationEncoding.equals("xyzw") || rotationEncoding.equals("XYZW") || rotationEncoding.equals("reversedQuat"))
            {
                reverseQuats();
            }

        }
    }

    public void decodeContent(String data)
    {
        calculateConfigSize();
        configs = new ConfigList(configSize);
        configs.decodeContent(data);
        if (rotationEncoding != null)
        {
            if (rotationEncoding.equals("axisangles"))
            {
                convertFromAxisAngles();
            }
        }
    }

    private void convertFromAxisAngles()
    {
        for (int i = 0; i < configs.size(); i++)
        {
            float[] conf = configs.getConfig(i);
            int startIndex = (hasRootTranslation) ? 3 : 0;
            for (int ri = startIndex; ri < configSize; ri += stride)
            {
                Quat4f.setFromAxisAngle4f(conf, ri, conf, ri);
            }
        }
    }

    private void reverseQuats()
    {
        for (int i = 0; i < configs.size(); i++)
        {
            float[] conf = configs.getConfig(i);
            int startIndex = (hasRootTranslation) ? 3 : 0;
            for (int ri = startIndex; ri < configSize; ri += stride)
            {
                Quat4f.setFromXYZW(conf, ri, conf, ri);
            }
        }
    }

    public SkeletonInterpolator subSkeletonInterpolator(int start)
    {
        return subSkeletonInterpolator(start, size());
    }

    /**
     * Creates an SkeletonInterpolator that blends from this at time t to target at time tTarget, over interval duration.
     */
    public SkeletonInterpolator blend(double tSource, double tTarget, SkeletonInterpolator target, double duration)
    {
        SkeletonInterpolator blend = new SkeletonInterpolator();
        blend.setPartIds(partIds.clone());
        blend.setConfigType(configType);
        blend.stride = stride;
        blend.hasRootTranslation = hasRootTranslation;
        blend.hasTranslation = hasTranslation;
        blend.hasRotation = hasRotation;
        blend.hasScale = hasScale;
        blend.hasVelocity = hasVelocity;
        blend.hasAngularVelocity = hasAngularVelocity;
        blend.rotationEncoding = rotationEncoding;
        blend.targetParts = targetParts;
        blend.configs = new ConfigList((int) (duration * 50));
        for (double t = 0; t <= duration; t += 1d / 50d)
        {
            float startConfig[] = new float[configSize];
            float endConfig[] = new float[configSize];
            float result[] = new float[configSize];
            float alpha = (float)(t / duration);
            getInterpolatedConfig(tSource+t, startConfig);
            target.getInterpolatedConfig(tTarget+t, endConfig);
            interpolateConfigs(result, alpha, startConfig, endConfig);
            blend.configs.addConfig(t, result);
        }
        return blend;
    }

    public SkeletonInterpolator subSkeletonInterpolator(int start, int end)
    {
        SkeletonInterpolator subSki = new SkeletonInterpolator();
        subSki.setPartIds(partIds.clone());
        subSki.setConfigType(configType);
        subSki.stride = stride;
        subSki.hasRootTranslation = hasRootTranslation;
        subSki.hasTranslation = hasTranslation;
        subSki.hasRotation = hasRotation;
        subSki.hasScale = hasScale;
        subSki.hasVelocity = hasVelocity;
        subSki.hasAngularVelocity = hasAngularVelocity;
        subSki.rotationEncoding = rotationEncoding;
        subSki.targetParts = targetParts;
        subSki.target = target;
        subSki.configs = configs.subConfigList(start, end);
        return subSki;
    }

    public void appendInterpolator(double startTime, SkeletonInterpolator ski)
    {
        ConfigList cl = new ConfigList(getConfigSize() + ski.getConfigSize());
        cl.addConfigs(getConfigList());
        cl.addConfigs(startTime, ski.getConfigList());
        setConfigList(cl);
    }

    /**
     * Creates a new SkeletonInterpolator from an XML encoded file. The first argument must be a
     * Resources object that determines the resource directory, whereas the seond argument must be
     * the filename relative to the resource directory.
     */
    public static SkeletonInterpolator read(Resources resources, String fileName) throws IOException
    {
        Reader reader = resources.getReader(fileName);
        XMLTokenizer tk = new XMLTokenizer(reader);
        SkeletonInterpolator ip = new SkeletonInterpolator(tk);
        reader.close();
        return ip;
    }

    /**
     * Creates a new SkeletonInterpolator from an XML encoded file. The first argument must be a the
     * resource directory, relative to the project's resource directory. The seond argument must be
     * the filename relative to the specified resource directory.
     */
    public static SkeletonInterpolator read(String resourceDir, String fileName) throws IOException
    {
        Resources resources = new Resources(resourceDir);
        Reader reader = resources.getReader(fileName);
        XMLTokenizer tk = new XMLTokenizer(reader);
        return new SkeletonInterpolator(tk);
    }

    /**
     * Creates a new SkeletonInterpolator from an XML encoded file. The specified file name must be
     * relative to the project's resource directory.
     */
    public static SkeletonInterpolator read(String fileName) throws IOException
    {
        Resources resources = new Resources("");
        Reader reader = resources.getReader(fileName);
        XMLTokenizer tk = new XMLTokenizer(reader);
        return new SkeletonInterpolator(tk);
    }

    /*
     * The XML Stag for XML encoding
     */
    private static final String XMLTAG = "SkeletonInterpolator";

    /**
     * The XML Stag for XML encoding -- use this static method when you want to see if a given
     * String equals the xml tag for this class
     */
    public static String xmlTag()
    {
        return XMLTAG;
    }

    /**
     * The XML Stag for XML encoding -- use this method to find out the run-time xml tag of an
     * object
     */
    @Override
    public String getXMLTag()
    {
        return XMLTAG;
    }

    /**
     * @return the targetParts
     */
    public VObject[] getTargetParts()
    {
        return targetParts;
    }

}
