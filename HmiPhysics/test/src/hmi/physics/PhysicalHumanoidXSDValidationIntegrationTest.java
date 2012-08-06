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
