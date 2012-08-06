package hmi.tts.util;

/**
 * Null implementation for PhonemeToVisemeMapping, maps all phonemes to the 0 viseme
 * @author welberge
 */
public class NullPhonemeToVisemeMapping implements PhonemeToVisemeMapping
{
    @Override
    public int getVisemeForPhoneme(int phon)
    {
        return 0;
    }
}
