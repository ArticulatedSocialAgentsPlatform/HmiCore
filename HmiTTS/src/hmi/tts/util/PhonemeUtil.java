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
