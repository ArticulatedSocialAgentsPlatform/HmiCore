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
package hmi.tts.mary;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import hmi.testutil.tts.AbstractTTSGeneratorTest;
import hmi.tts.TTSException;
import hmi.tts.mary5.MaryProsody;
import hmi.tts.mary5.MaryTTSGenerator;

import java.io.IOException;

import marytts.datatypes.MaryDataType;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Creates a MaryTTSGenerator, and runs the testcases for generic TTSGenerators on it.
 * @author Herwin
 */
public class MaryTTSGeneratorTest extends AbstractTTSGeneratorTest
{
    private static MaryTTSGenerator mttsG;

    @BeforeClass
    public static void setup() throws Exception
    {
        mttsG = new MaryTTSGenerator();
        ttsG = mttsG;        
    }

    @Before
    public void before()
    {
        mttsG.setVoice("cmu-slt-hsmm");
    }
    
    @Test
    public void testProsody() throws InterruptedException, TTSException
    {
        MaryProsody pros = mttsG.speakBML("Hello world");
        assertEquals(pros.getF0().length, pros.getRmsEnergy().length);
    }

    @Test
    public void testProsodyBreaks() throws IOException, TTSException
    {
        MaryProsody pros1 = mttsG.speak(mttsG.getSSMLStartTag()+"test<break time=\"3.0s\"/></speak>",MaryDataType.SSML);
        MaryProsody pros2 = mttsG.speak(mttsG.getSSMLStartTag()+"test</speak>",MaryDataType.SSML);
        assertThat(pros1.getF0().length, greaterThan(pros2.getF0().length));
    }
}
