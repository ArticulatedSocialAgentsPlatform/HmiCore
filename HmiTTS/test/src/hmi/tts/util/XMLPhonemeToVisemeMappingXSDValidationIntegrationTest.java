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

import java.io.File;
import java.util.Collection;

import org.junit.Before;
import org.junit.runners.Parameterized.Parameters;

import hmi.testutil.xml.XSDValidationTest;
import hmi.util.Resources;

/**
 * Validates the phonemetovisememapping in /HmiResource/HmiHumanoidFaceControl/resource/Humanoids/shared/phoneme2viseme/
 * against the xsd.  
 * @author welberge
 */
public class XMLPhonemeToVisemeMappingXSDValidationIntegrationTest extends XSDValidationTest
{
    private static final Resources MAPPING_XSD_RES = new Resources("xsd");
    private static final String MAPPING_XSD = "phonemetovisememapping.xsd";
    private static final String MAPPING_DIRS[] = {
        System.getProperty("shared.project.root")+ "/HmiResource/HmiHumanoidEmbodimentShared/resource/Humanoids/shared/phoneme2viseme/"};
    
    @Before
    public void setup()
    {
        xsdReader = MAPPING_XSD_RES.getReader(MAPPING_XSD);
    }

    @Parameters
    public static Collection<Object[]> configs()
    {
        return configs(MAPPING_DIRS);
    }

    public XMLPhonemeToVisemeMappingXSDValidationIntegrationTest(String label, File f)
    {
        super(label, f);
    }
}
