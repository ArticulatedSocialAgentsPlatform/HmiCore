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
package hmi.tts.util;

/**
 * Phoneme utilities
 * @author Herwin
 *
 */
public final class PhonemeUtil
{
    private PhonemeUtil(){}
    public static int phonemeStringToInt(String phonemeString)
    {
        int phonemeNr = 0;
        for (int i = 0; i < phonemeString.length(); i++)
        {
            char c = phonemeString.charAt(i);
            int ph = c;
            for (int j = 0; j < i; j++)
            {
                ph <<= 8;
            }
            phonemeNr += ph;
        }
        return phonemeNr;
    }
    
    public static String phonemeIntToString(int phoneme)
    {
        StringBuffer buf = new StringBuffer();
        for(int i=0;i<4;i++)
        {
            int ph = phoneme >> (i*8);
            char ch = (char)(ph&255);
            if(ch>0)
            {
                buf.append(ch);
            }
        }
        return buf.toString();
    }
}
