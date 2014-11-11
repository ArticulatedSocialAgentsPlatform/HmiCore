package hmi.faceanimation;

import hmi.animation.ConfigList;
import hmi.math.Vecf;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import lombok.Data;

import com.google.common.collect.ImmutableList;

/**
 * A MorphInterpolator is an interpolator for simultaneous animation of several selected Morph targets of
 * a face. Interpolation is linear.
 */
public class MorphInterpolator extends XMLStructureAdapter
{
    private List<String> parts;
    private ConfigList configs = new ConfigList(0);
    private float[] lowerConfig, upperConfig;
    
    // cached values for the interpolation process:
    private int lowerIndex, upperIndex;

    private double lowerTime, upperTime, interval;
    
    @Data
    public class MorphFrame
    {
        final String[] morphTargets;
        final float[] weights;
    }

    public List<String> getParts()
    {
        return ImmutableList.copyOf(parts);
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
        } 
        
        lowerTime = configs.getTime(lowerIndex);
        upperTime = configs.getTime(upperIndex);

        lowerConfig = configs.getConfig(lowerIndex);
        upperConfig = configs.getConfig(upperIndex);

        interval = upperTime - lowerTime;
        float alpha = (interval <= 0.0f) ? 0.0f : (float) ((t - lowerTime) / interval);
        return alpha;
    }
    
    public MorphFrame interpolate(double time)
    {
        float alpha = getInterpolationConfigs(time);
        float []current = new float[lowerConfig.length];
        Vecf.interpolate(current, lowerConfig, upperConfig, alpha);
        return new MorphFrame(parts.toArray(new String[parts.size()]), current);
    }
    /**
     * decodes the XML attributes
     */
    @Override
    public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer)
    {
        parts = Arrays.asList(decodeStringArray(getRequiredAttribute("parts", attrMap, tokenizer)));
        super.decodeAttributes(attrMap, tokenizer);
    }
    
    @Override
    public void decodeContent(XMLTokenizer xmlTokenizer) throws IOException
    {
        configs = new ConfigList(parts.size());
        configs.decodeContent(xmlTokenizer);
    }

    private static final String XMLTAG = "MorphInterpolator";

    public static String xmlTag()
    {
        return XMLTAG;
    }

    @Override
    public String getXMLTag()
    {
        return XMLTAG;
    }

}
