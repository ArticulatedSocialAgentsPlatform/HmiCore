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

import hmi.math.Quat4f;
import hmi.math.Vec3f;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * A ConfigList defines a sequence of configurations of type float[] plus
 * corresponding timestamps of type double.
 */
public class ConfigList extends XMLStructureAdapter implements Cloneable
{

    private double[] time; // timestamps for the configs. time.length ==
                           // configList.length
    private float[][] configList; // configList[i] is a float[] containing the
                                  // i-th configuration of the list
    private int listSize; // configList[0] .. configList[listSize-1] contain
                          // actual list data.
    private int arraySize; // the (current) length of the configList array.
    private static final int DEFAULTARRAYSIZE = 8; // default array size, should
                                                   // be > 0
    private int configSize; // length of a configList element, in number of
                            // floats.

    public ConfigList(int configSize)
    {
        arraySize = DEFAULTARRAYSIZE;
        time = new double[arraySize];
        configList = new float[arraySize][];
        listSize = 0;
        this.configSize = configSize;
    }    

    public ConfigList copy()
    {
        ConfigList c = new ConfigList(configSize);
        c.time = time.clone();
        c.listSize = listSize;
        c.arraySize = arraySize;
        c.configList = new float[arraySize][];
        for (int i = 0; i < listSize; i++)
        {
            c.configList[i] = new float[configSize];
            for (int j = 0; j < configSize; j++)
            {
                c.configList[i][j] = configList[i][j];
            }
        }
        return c;
    }

    public ConfigList(XMLTokenizer tokenizer) throws IOException
    {
        this(0);
        readXML(tokenizer);
    }

    
    /**
     * Returns the (uniform) size of configs, in number of floats.
     */
    public int getConfigSize()
    {
        return configSize;
    }

    /**
     * returns the size of the list
     */
    public int size()
    {
        return listSize;
    }

    /**
     * Returns the time for config i
     */
    public double getTime(int i)
    {
        return time[i];
    }

    public void mirrorRotation(int index)
    {
        for (int i = 0; i < listSize; i++)
        {
            float[] config = configList[i];
            float q[] = Quat4f.getQuat4f();
            Quat4f.set(q, 0, config, index);
            Quat4f.set(config, index, q[Quat4f.s], q[Quat4f.x], -q[Quat4f.y],
                    -q[Quat4f.z]);
        }
    }

    public void mirrorTranslation(int index)
    {
        for (int i = 0; i < listSize; i++)
        {
            float[] config = configList[i];
            float v[] = Vec3f.getVec3f();
            Vec3f.set(v, 0, config, index);
            Vec3f.set(config, index, -v[Vec3f.X], v[Vec3f.Y], -v[Vec3f.Z]);
        }
    }
    
    public ConfigList subConfigList(int start, int end)
    {
        ConfigList cl = new ConfigList(configSize);
        for(int i=start;i<end;i++)
        {
            cl.addConfig(getTime(i), getConfig(i));
        }
        return cl;
    }
    
    /**
     * Returns the Config at index i
     */
    public float[] getConfig(int i)
    {
        return configList[i];
    }

    public double getStartTime()
    {
        return (listSize == 0) ? 0.0 : time[0];
    }

    public double getEndTime()
    {
        return (listSize == 0) ? 0.0 : time[listSize - 1];
    }

    /**
     * Adds a VPartsConfig at the appropriate place, depending on the time value
     * The value is inserted at the position determined by findLowerIndex(time)
     * + 1.
     */
    public void addConfig(double t, float[] conf)
    {
        ensureArraySize(listSize + 1);
        int pos = findInsertIndex(t);
        for (int p = listSize; p > pos; p--)
        {
            configList[p] = configList[p - 1];
            time[p] = time[p - 1];
        }
        time[pos] = t;
        configList[pos] = conf;
        listSize++;
    }

    /*
     * (re)allocates the configs array such that the array size is greate or
     * equal than "requestedSize"
     */
    private void ensureArraySize(int requestedSize)
    {
        if (requestedSize <= arraySize)
            return;
        while (arraySize < requestedSize)
            arraySize *= 2;
        float[][] newConfigList = new float[arraySize][];
        double[] newTime = new double[arraySize];
        System.arraycopy(configList, 0, newConfigList, 0, listSize);
        System.arraycopy(time, 0, newTime, 0, listSize);
        configList = newConfigList;
        time = newTime;
    }

    /*
     * Finds the insertion place for a specified time t. returns index such that
     * in general: time[index-1] <= t < time[index]. Special cases: when size()
     * == 0, or when t < time[0], then &quot;index&quot is considered to be 0.
     * On the other hand, when time[listSize-1] <= t then &quot;index&quot is
     * defined to be listSize. Example: for a list with listSize==5, and with
     * time stamps as follows: [100, 200, 200, 200, 300], we have:
     * findInsertIndex(0) = findInsertIndex(99) = 0 findInsertIndex(100) =
     * findInsertIndex(150) = 1 findInsertIndex(200) = findInsertIndex(250) = 4
     * findInsertIndex(300) = findInsertIndex(500) = 5
     */
    private int findInsertIndex(double t)
    {
        int low = 0;
        int high = listSize - 1;
        if (listSize == 0 || t < time[low])
            return 0; // empty list, or t before first element
        if (t >= time[high])
            return listSize; // t at or after last element
        // invariant: time[low] <= t < time[high]
        while (high - low > 1)
        { // iterate until high == low+1
            int probe = (high + low) >>> 1;
            if (t < time[probe])
            {
                high = probe;
            }
            else
            {
                low = probe;
            }
        }
        return high;
    }

    /**
     * Appends a String of signature attributes to buf
     */
    @Override
    public StringBuilder appendAttributeString(StringBuilder buf,
            XMLFormatting fmt)
    {
        appendAttribute(buf, "configSize", configSize);
        return buf;
    }

    /**
     * Decodes a single attribute
     */
    @Override
    public boolean decodeAttribute(String attrName, String valCode,
            XMLTokenizer tokenizer)
    {
        if (attrName.equals("configSize"))
        {
            configSize = decodeInt(valCode);
            return true;
        }
        else
        {
            return super.decodeAttribute(attrName, valCode, tokenizer);
        }
    }

    /**
     * Appends the config elements as XML content.
     */
    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt)
    {
        for (int i = 0; i < listSize; i++)
        {
            appendNewLine(buf, fmt);
            buf.append(Double.toString(time[i]));
            float[] c = configList[i];
            for (int j = 0; j < c.length; j++)
            {
                buf.append(' ');
                buf.append(Float.toString(c[j]));
            }
        }
        return buf;
    }

    /**
     * Decodes XML content, and converts it into the double time values and
     * float cofig data.
     */
    @Override
    public void decodeContent(XMLTokenizer xmlTokenizer) throws IOException
    {
        String data = xmlTokenizer.takeCharData();
        decodeContent(data);
    }

    public void decodeContent(String data)
    {
        StringTokenizer tokenizer = new StringTokenizer(data);
        double t;
        float[] conf;
        while (tokenizer.hasMoreTokens())
        {
            t = Double.parseDouble(tokenizer.nextToken());

            // performance test
            // t = 0;
            // tokenizer.nextToken();

            conf = new float[configSize];
            for (int i = 0; i < configSize; i++)
            {

                conf[i] = (float) Double.parseDouble(tokenizer.nextToken());
                // performance test
                // tokenizer.nextToken();
            }
            addConfig(t, conf);
        }

        /*
         * implementation based on String.split, this is even slower, at least
         * on very big Strings int index = 0; String splitData[] =
         * data.split("[\\s]+"); if(splitData[0].length()==0)index++; double t;
         * float[] conf; while (index<splitData.length) { t =
         * Double.parseDouble(splitData[index]); index++; conf = new
         * float[configSize]; for (int i=0; i<configSize; i++) { conf[i] =
         * (float) Double.parseDouble(splitData[index]); index++; } addConfig(t,
         * conf); }
         */
    }

    /*
     * The XML Stag for XML encoding
     */
    private static final String XMLTAG = "ConfigList";

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

}
