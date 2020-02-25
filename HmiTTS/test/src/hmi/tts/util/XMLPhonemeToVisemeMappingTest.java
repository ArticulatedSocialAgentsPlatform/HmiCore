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
