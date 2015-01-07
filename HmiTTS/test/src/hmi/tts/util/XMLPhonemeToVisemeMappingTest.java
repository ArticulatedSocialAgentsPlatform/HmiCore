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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test case for the PhonemeToVisemeMapping
 * @author Herwin
 *
 */
public class XMLPhonemeToVisemeMappingTest
{
    XMLPhonemeToVisemeMapping mapping = new XMLPhonemeToVisemeMapping();
    
    @Before
    public void setup()
    {
        String str = "<PhonemeToVisemeMapping name=\"sampade2ikp\">"+
                "<phoneme viseme=\"1\" phoneme=\"p\"/>" +
                "<phoneme viseme=\"1\" phoneme=\"b\"/>" +
                "<phoneme viseme=\"2\" phoneme=\"t\"/>"+
                "<phoneme viseme=\"3\" phoneme=\"@n\"/>"+
                "</PhonemeToVisemeMapping>";
        mapping.readXML(str);    
    }
    
    @Test
    public void testt()
    {
        assertEquals(2, mapping.getVisemeForPhoneme(PhonemeUtil.phonemeStringToInt("t")));
    }
    
    @Test
    public void testp()
    {
        assertEquals(1, mapping.getVisemeForPhoneme(PhonemeUtil.phonemeStringToInt("p")));
    }
    
    @Test
    public void testb()
    {
        assertEquals(1, mapping.getVisemeForPhoneme(PhonemeUtil.phonemeStringToInt("b")));
    }
    
    @Test
    public void testatn()
    {
        assertEquals(3, mapping.getVisemeForPhoneme(PhonemeUtil.phonemeStringToInt("@n")));
    }
}
