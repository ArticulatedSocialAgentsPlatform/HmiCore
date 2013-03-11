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
package hmi.faceanimation.model;

import hmi.xml.XMLStructureAdapter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A MPEG-4 FA Configuration (set of values for FAPs).
 * 
 * @author PaulRC
 */
public class MPEG4Configuration extends XMLStructureAdapter implements Configuration
{
    private Integer[] values;

    /**
     * Constructor
     */
    public MPEG4Configuration()
    {
        int numFaps = MPEG4.getFAPs().size();
        values = new Integer[numFaps];
    }

    public void setValue(int index, Integer value)
    {
        values[index] = value;
    }

    public Integer getValue(int index)
    {
        return values[index];
    }

    /**
     * Sets the values. This must be a int[] with the right length.
     * 
     * @param values
     * @throws Exception
     */
    public void setValues(Integer[] values)
    {
        if (values.length == this.values.length)
        {
            System.arraycopy(values, 0, this.values, 0, values.length);            
        }
        else
        {
            System.err.println("wrong number of values");
        }
    }

    /**
     * Add given values to current configuration. This must be a int[] with the right length.
     * 
     * @param values
     */
    public void addValues(MPEG4Configuration config)
    {
        Integer[] values = config.getValues();
        if (values.length == this.values.length)
        {
            for (int i = 0; i < values.length; i++)
            {
                if (this.values[i] == null)
                {
                    this.values[i] = values[i];
                }
                else if (values[i] != null)
                {
                    this.values[i] = Integer.valueOf(this.values[i].intValue() + values[i].intValue());
                }
            }
        }
        else
        {
            System.err.println("wrong number of values");
        }
    }

    /**
     * multiply values with given factor
     * 
     * @param factor
     */
    public void multiply(float factor)
    {
        for (int i = 0; i < values.length; i++)
        {
            if (values[i] != null)
            {
                values[i] = Integer.valueOf((int) ((float) values[i].intValue() * factor));
            }
        }
    }

    /**
     * Remove given values from current configuration. This must be a int[] with the right length.
     * 
     * @param values
     */
    public void removeValues(MPEG4Configuration config)
    {
        Integer[] values = config.getValues();
        if (values.length == this.values.length)
        {
            for (int i = 0; i < values.length; i++)
            {
                if (this.values[i] == null)
                {
                    this.values[i] = values[i];
                }
                else if (values[i] != null)
                {
                    this.values[i] = Integer.valueOf(this.values[i].intValue() - values[i].intValue());
                }
            }
        }
        else
        {
            System.err.println("wrong number of values");
        }
    }

    public Integer[] getValues()
    {
        return values;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        for (FAP fap : MPEG4.getFAPs().values())
        {
            Integer value = values[fap.getIndex()];
            if (value == null)
                continue;
            sb.append(fap.getName() + "=" + value + " ");

            // sb.append("F");
            // sb.append(fap.number);
            // sb.append(" ");
            // sb.append("[" + (value - 5) + ", " + (value + 5) + "], ");
        }

        if (sb.length() > 0)
            sb.append("\n");

        return sb.toString();
    }

    public void saveToFAPFile(File file) throws IOException
    {
        final int numberOfFrames = 100;

        FileWriter fw = new FileWriter(file);
        StringBuilder sb = new StringBuilder();
        sb.append("2.1 dummy 25 " + numberOfFrames + "\n");

        for (int j = 0; j < numberOfFrames; j++)
        {
            for (int i = 0; i < values.length; i++)
            {
                if (i == 0 || i == 1 || values[i] == null)
                    sb.append(0);
                else
                    sb.append(1);
                if (i < values.length)
                    sb.append(' ');
            }

            // Frame number
            sb.append("\n");
            sb.append(j);
            sb.append(' ');
            for (int i = 0; i < values.length; i++)
            {
                if (i == 0 || i == 1 || values[i] == null)
                    continue;
                sb.append(values[i]);
                sb.append(' ');
            }

            sb.append("\n");
        }

        fw.write(sb.toString());
        fw.close();
    }

    /*
     * The XML Stag for XML encoding
     */
    private static final String XMLTAG = "MPEG4Configuration";

    /**
     * The XML Stag for XML encoding -- use this static method when you want to see if a given String equals the xml tag for this class
     */
    public static String xmlTag()
    {
        return XMLTAG;
    }

    /**
     * The XML Stag for XML encoding -- use this method to find out the run-time xml tag of an object
     */
    @Override
    public String getXMLTag()
    {
        return XMLTAG;
    }

}
