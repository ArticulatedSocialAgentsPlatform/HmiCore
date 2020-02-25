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
package hmi.tts.sapi5;

import hmi.testutil.tts.AbstractTTSGeneratorTest;
import hmi.util.OS;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;

/**
 * Unit test cases for the SAPI5TTSGenerator
 * @author welberge
 */
public class SAPI5TTSGeneratorTest extends AbstractTTSGeneratorTest
{
    private SAPI5TTSGenerator sapiTtsG;
    @Before
    public void setup()
    {
        Assume.assumeTrue(OS.equalsOS(OS.WINDOWS));
        sapiTtsG  = new SAPI5TTSGenerator();        
        ttsG = sapiTtsG;    //ignore findbugs warning, this is static to test Mary :(
    }
    
    @After
    public void cleanup()
    {
        if(sapiTtsG!=null)
        {
            sapiTtsG.cleanup();
        }
    }
}
