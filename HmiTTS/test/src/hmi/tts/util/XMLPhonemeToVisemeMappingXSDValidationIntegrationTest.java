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
