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
package hmi.physics;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.runners.Parameterized.Parameters;

import hmi.testutil.xml.XSDValidationTest;
import hmi.util.Resources;

/**
 * Validates armandia and blueguy physical models
 * @author welberge
 *
 */
@Ignore
public class PhysicalHumanoidXSDValidationIntegrationTest extends XSDValidationTest
{
    
    private static final Resources PH_XSD_RES = new Resources("xsd");
    private static final String PH_XSD = "physicalhuman.xsd";
    private static final String PH_DIRS[] = {
        System.getProperty("shared.project.root")+"/HmiResource/HmiHumanoidEmbodiments/resource/Humanoids/armandia/physicalmodels", 
        System.getProperty("shared.project.root")+"/HmiResource/HmiHumanoidEmbodiments/resource/Humanoids/blueguy/physical"
    };    
    
    @Override
    protected InputStream getXSDStream(String fileName)
    {
       return PH_XSD_RES.getInputStream(fileName);       
    }
    
    @Before
    public void setup()
    {
        xsdReader = PH_XSD_RES.getReader(PH_XSD);        
    }
    
    @Parameters
    public static Collection<Object[]> configs()
    {
        return configs(PH_DIRS);
    }
    
    public PhysicalHumanoidXSDValidationIntegrationTest(String label, File f)
    {
        super(label, f);        
    }
}
