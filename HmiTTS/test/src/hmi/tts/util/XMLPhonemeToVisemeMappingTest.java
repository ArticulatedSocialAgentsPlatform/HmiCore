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
