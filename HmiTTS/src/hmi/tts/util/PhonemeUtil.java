package hmi.tts.util;

/**
 * Phoneme utilities
 * @author Herwin
 *
 */
public final class PhonemeUtil
{
    private PhonemeUtil(){}
    public static int phonemeStringToInt(String phonemeString)
    {
        int phonemeNr = 0;
        for (int i = 0; i < phonemeString.length(); i++)
        {
            char c = phonemeString.charAt(i);
            int ph = c;
            for (int j = 0; j < i; j++)
            {
                ph <<= 8;
            }
            phonemeNr += ph;
        }
        return phonemeNr;
    }
}
