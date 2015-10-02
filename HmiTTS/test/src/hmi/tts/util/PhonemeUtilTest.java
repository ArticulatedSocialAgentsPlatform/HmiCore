package hmi.tts.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for PhonemeUtil
 * @author herwinvw
 *
 */
public class PhonemeUtilTest
{
    @Test
    public void testToIntAndBack()
    {
        int ph = PhonemeUtil.phonemeStringToInt("i:");     
        assertEquals("i:",PhonemeUtil.phonemeIntToString(ph));
    }
    
    @Test
    public void testToStringAndBack()
    {
        String ph = PhonemeUtil.phonemeIntToString(14953);
        assertEquals(14953,PhonemeUtil.phonemeStringToInt(ph));
    }
}
