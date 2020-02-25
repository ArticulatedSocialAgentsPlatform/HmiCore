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
import hmi.xml.XMLFormatting;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import lombok.extern.slf4j.Slf4j;

/**
 * Contains a single pose in a config and a configType describing what the
 * numbers in the config mean. Poses can be copied to or from an array of VObjects
 * set as target
 * 
 * @author welberge
 */
@Slf4j
public class SkeletonPose extends XMLStructureAdapter// implements Ident
{
    private static final String[] empty_PartIds = new String[0];
    private String[] partIds = empty_PartIds;

    private float[] config;
    private String configType;
    private int configSize; // length of a config, in number of floats.
    private int stride; // number of floats for a single joint, including translation for a TR format
                        // but not the possible rootTranslation for the T1R format.
    private boolean hasRootTranslation, hasTranslation, hasRotation, hasScale;
    private boolean hasVelocity, hasAngularVelocity;
    private String rotationEncoding = null; // default is quaternions not really used, except for XML decoding
    private VObject[] targetParts; // references to the target VObjects.
    private String id;

    
    /**
     * Creates a new, uninitialized, SkeletonPose
     */
    public SkeletonPose(XMLTokenizer tokenizer) throws IOException
    {
        super();
        readXML(tokenizer);
    }

    /**
     * Creates a shallow copy that is not connected to this SkeletonPose's target
     * The copy shares this SkeletonPose's partIds and config.
     */
    public SkeletonPose untargettedCopy()
    {
        SkeletonPose copy = new SkeletonPose(partIds, config, configType);
        return copy;
    }
    
    /**
     * Creates a shallow copy that is not connected to this SkeletonPose's target
     * The copy shares copies of this SkeletonPose's partIds and config.
     */
    public SkeletonPose untargettedDeepCopy()
    {
        String[] newPartIds = new String[partIds.length];
        System.arraycopy(partIds,0,newPartIds,0,partIds.length);
        float newConfig[]=new float[config.length];
        System.arraycopy(config,0,newConfig,0,config.length);
        return new SkeletonPose(newPartIds, newConfig, configType);
    }

    /**
     * Creates a new SkeletonPose for a specified ConfigList, VParts,
     * and Config type. The Config type should be a String like "T1R" or "R", or
     * "TRSVW".
     */
    public SkeletonPose(String[] partIds, float[] config, String configType)
    {
        super();
        setPartIds(partIds);
        setConfig(config);
        setConfigType(configType);
        calculateConfigSize();
    }

    /**
     * Creates a new SkeletonPose for a specified ConfigList, VParts,
     * and Config type. The Config type should be a String like "T1R" or "R", or
     * "TRSVW".
     * ==> This SkeletonPose is not yet coupled to any Skeleton.
     */
    public SkeletonPose(String id, String[] partIds, String configType)
    {
        super();
        setId(id);
        setPartIds(partIds);
        setConfigType(configType);
        calculateConfigSize();
        config = new float[configSize];
        setConfig(config);
    }

    /**
     * Creates a new SkeletonPose for a specified ConfigList, VParts,
     * and Config type. The Config type should be a String like "T1R" or "R", or
     * "TRSVW".
     * ==> This SkeletonPose is not yet coupled to any Skeleton.
     */
    public SkeletonPose(String id, List<String> partIds, String configType)
    {
        super();
        setId(id);
        setPartIds(partIds.toArray(new String[partIds.size()]));
        setConfigType(configType);
        calculateConfigSize();
        config = new float[configSize];
        setConfig(config);
    }

    /* Create a SkeletonPose, and link to targets, most likely, Skeleton VJoints */
    private SkeletonPose(String id, VObject[] targets, String configType)
    {
        super();
        setId(id);
        targetParts = targets;
        setPartIds(targets);
        setConfigType(configType);
        calculateConfigSize();
        config = new float[configSize];
        setConfig(config);
    }

    private void mirrorParts(int i, VJoint root)
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
                targetParts[i] = root.getPart(partIds[i]);
            }
        }
    }

    /**
     * Mirrors all joint rotations on the XY plane, switches left/right partIds
     */
    public void mirror(VJoint root)
    {
        int index = 0;
        if (hasRootTranslation) index += 3;
        for (int i = 0; i < partIds.length; i++)
        {
            if (partIds[i].startsWith("l_"))
            {
                partIds[i] = partIds[i].replace("l_", "r_");
                if(root!=null)
                {
                    mirrorParts(i, root);
                }
            }
            else if (partIds[i].startsWith("r_"))
            {
                partIds[i] = partIds[i].replace("r_", "l_");
                if(root!=null)
                {
                    mirrorParts(i, root);
                }
            }

            if (hasTranslation)
            {
                index += 3;
            }
            if (hasRotation)
            {
                log.debug("mirroring {}, index {}", partIds[i], index);

                // configs.mirror(index);
                float q[] = Quat4f.getQuat4f();
                Quat4f.set(q, 0, config, index);
                Quat4f.set(config, index, q[Quat4f.s], q[Quat4f.x], -q[Quat4f.y], -q[Quat4f.z]);
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
     * Creates a new, unnamed, SkeletonPose for all parts reachable from the Skeleton root
     * 
     */
    public SkeletonPose(Skeleton skeleton, String configType)
    {
        this(null, skeleton, configType);
    }

    /**
     * Creates a new SkeletonPose for all parts reachable from the Skeleton root
     * 
     */
    public SkeletonPose(String id, Skeleton skeleton, String configType)
    {
        this(id, skeleton, VJointUtils.transformToSidList(skeleton.getRoot().getParts()), configType);
    }

    /**
     * Creates a new SkeletonPose for a list of VJoints, specified as parts reachable from
     * the specified root, with specified ids or sids. The latter specified as a String array
     */
    public SkeletonPose(String id, Skeleton skeleton, String[] partIdents, String configType)
    {
        this(id, skeleton, Arrays.asList(partIdents), configType);
    }

    /**
     * Creates a new SkeletonPose for a list of VJoints, specified as parts reachable from
     * the specified root, with specified ids or sids. The latter specified as a String List
     */
    public SkeletonPose(String id, Skeleton skeleton, List<String> partIdents, String configType)
    {
        super();
        setId(id);
        List<VJoint> joints = skeleton.getRoot().getParts(partIdents);
        
        int i=0;
        for (VJoint jnt : joints)
        {
            if (jnt == null) log.error("Null VJoint {}",i);
            i++;
        }

        targetParts = (skeleton.getRoot().getParts(partIdents)).toArray(new VObject[partIdents.size()]);

        setPartIds(targetParts);
        setConfigType(configType);
        calculateConfigSize();
        config = new float[configSize];
        setConfig(config);
    }

    /**
     * return the id of this SkeletonPose, or null, if not set.
     * The id String is interned.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id for this SkeletonPose, as an interned String.
     */
    public void setId(String id)
    {
        this.id = (id == null) ? null : id.intern();
    }

    /*
     * Sets the list of VObject ids
     */
    private void setPartIds(String[] partIds)
    {
        this.partIds = partIds;
    }

    /**
     * Returns the list of ids of the VObject parts that are influenced by this
     * interpolator.
     */
    public String[] getPartIds()
    {
        return partIds;
    }

    /*
     * Sets the VObject part ids, obtained from the targets themselves.
     */
    private void setPartIds(VObject[] targets)
    {
        partIds = new String[targets.length];
        for (int i = 0; i < targets.length; i++)
        {
            if (targets[i] == null) log.error("SkeletonPose.setPartIds, null target for index: " + i);
            partIds[i] = targets[i].getSid();
        }
    }

    /* calculate the length, in # floats, of a config for this SkeletonPose type */
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

    /**
     * Sets the ConfigList, defining the VPartsConfigs to be interpolated.
     */
    public void setConfig(float[] config)
    {
        this.config = config;
    }

    /**
     * Gets the Config list
     */
    public float[] getConfig()
    {
        return config;
    }

    /**
     * Returns the configuration type, as encoded conform VOBject types.
     */
    public String getConfigType()
    {
        return configType;
    }

    private void setConfigType(String configType)
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
     * returns a String that denotes the rotation encoding
     */
    public String getRotationEncoding()
    {
        return rotationEncoding;
    }

    /**
     * Returns the (uniform) size of pose, in number of floats.
     */
    public int getConfigSize()
    {
        return (config == null) ? 0 : config.length;
    }

    /**
     * equivalent to getConfigSize()
     */
    public int size()
    {
        return (config == null) ? 0 : config.length;
    }

    /**
     * Sets the VObjects from the specified VObject array as target for this SkeletonPose.
     * A lookup is performed for parts of the target with Id/Sid/Name as defined by the
     * partIds for this pose.
     */
    public void setTargets(VObject targets[])
    {
        targetParts = new VObject[partIds.length];
        for (int i = 0; i < partIds.length; i++)
        {
            for (int j = 0; j < targets.length; j++)
            {
                if ((targets[j].getId() != null && targets[j].getId().equals(partIds[i]))
                        || (targets[j].getSid() != null && targets[j].getSid().equals(partIds[i])))
                {
                    targetParts[i] = targets[j];
                    log.debug("targetParts[{}]={}", i, partIds[i]);
                }
            }
        }
    }

    // XXX: duplicate in SkeletonInterpolator.. Perhaps factor out a Signature for both?
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
     * @param jointIds
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
        float src[] = config;
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
        config = dst;
        this.configSize = configSize;
        
        if (targetParts != null)
        {
            filterTargetParts(joints);
        }
        setPartIds(newPartIds.toArray(new String[0]));
        config = dst;
        if (removeRoot)
        {
            hasRootTranslation = false;
            if (configType.startsWith("T1"))
            {
                configType = configType.substring(2);
            }
        }
        
    }

    /* Deprecated name for toSkeleton */
    public void setToTarget()
    {
        toSkeleton();
    }

    /**
     * Copies the current pose config to the VObject targets
     */
    public void toSkeleton() // copyToTargets
    {
        int index = 0;
        if (hasRootTranslation && targetParts.length > 0)
        {
            targetParts[0].setTranslation(config);
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
            if (hasVelocity)
            {
                targetParts[i].setVelocity(config, index);
                index += 3;
            }
            if (hasAngularVelocity)
            {
                targetParts[i].setAngularVelocity(config, index);
                index += 3;
            }
        }
    }

    /* deprecated name for fromSkeleton */
    public void setFromTarget()
    {
        fromSkeleton();
    }

    /**
     * Copies the current pose config from the status of the VObject targets
     */
    public void fromSkeleton() // copyFromTargets
    {
        int index = 0;
        if (hasRootTranslation && targetParts.length > 0)
        {
            targetParts[0].getTranslation(config);
            index += 3;
        }
        for (int i = 0; i < targetParts.length; i++)
        {
            if (hasTranslation)
            {
                targetParts[i].getTranslation(config, index);
                index += 3;
            }
            if (hasRotation)
            {
                targetParts[i].getRotation(config, index);
                index += 4;
            }
            if (hasScale)
            {
                targetParts[i].getScale(config, index);
                index += 3;
            }
            if (hasVelocity)
            {
                targetParts[i].getVelocity(config, index);
                index += 3;
            }
            if (hasAngularVelocity)
            {
                targetParts[i].getAngularVelocity(config, index);
                index += 3;
            }
        }
    }

    @Override
    public StringBuilder appendAttributeString(StringBuilder buf, XMLFormatting fmt)
    {
        appendAttribute(buf, "encoding", configType);
        appendAttribute(buf, "rotationEncoding", rotationEncoding);
        appendAttribute(buf, "parts", partIds, ' ', fmt, 60);
        return buf;
    }

    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt)
    {

        buf.append('\n');
        appendSpaces(buf, fmt);
        
        buf.append(config[0]);
        for (int i = 1; i < configSize; i++)
        {
            buf.append(' ');
            buf.append(config[i]);
        }
        return buf;
    }

    /**
     * decodes the XML attributes
     */
    @Override
    public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer)
    {
        id = getOptionalAttribute("id", attrMap, id);
        String parts = getRequiredAttribute("parts", attrMap, tokenizer);
        partIds = decodeStringArray(parts);
        String encoding = getRequiredAttribute("encoding", attrMap, tokenizer);
        setConfigType(encoding);
        rotationEncoding = getOptionalAttribute("rotationEncoding", attrMap, null);
        super.decodeAttributes(attrMap, tokenizer);
    }

    /**
     * Decodes XML content, and converts it into the float config data.
     */
    @Override
    public void decodeContent(XMLTokenizer xmlTokenizer) throws IOException
    {
        calculateConfigSize();
        if (xmlTokenizer.atCharData())
        {
            String data = xmlTokenizer.takeCharData();
            StringTokenizer tokenizer = new StringTokenizer(data);
            while (tokenizer.hasMoreTokens())
            {
                config = new float[configSize];
                for (int i = 0; i < configSize; i++)
                {
                    config[i] = (float) Double.parseDouble(tokenizer.nextToken());
                }
            }
            if (rotationEncoding != null)
            {
                if (rotationEncoding.equals("axisangles"))
                {
                    convertFromAxisAngles();
                }
            }
        }
    }

    private void convertFromAxisAngles()
    {
        int startIndex = (hasRootTranslation) ? 3 : 0;
        for (int ri = startIndex; ri < configSize; ri += stride)
        {
            Quat4f.setFromAxisAngle4f(config, ri, config, ri);
        }
    }

    /*
     * The XML Stag for XML encoding
     */
    private static final String XMLTAG = "SkeletonPose";

    /**
     * The XML Stag for XML encoding -- use this static method when you want to
     * see if a given String equals the xml tag for this class
     */
    public static String xmlTag()
    {
        return XMLTAG;
    }

    /**
     * The XML Stag for XML encoding -- use this method to find out the run-time
     * xml tag of an object
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
        for (int i = 0; i < configSize; i++)
            dataOut.writeFloat(config[i]);
    }

    /**
     * Reads a binary encoding from dataIn
     */
    public void readBinary(DataInput dataIn) throws IOException
    {
        for (int i = 0; i < configSize; i++)
            config[i] = dataIn.readFloat();
    }

}
