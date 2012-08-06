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
        System.getProperty("shared.project.root")+ "/HmiResource/HmiHumanoidFaceControl/resource/Humanoids/shared/phoneme2viseme/"};
    
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
