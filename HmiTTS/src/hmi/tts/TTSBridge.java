package hmi.tts;

import java.io.IOException;

/**
 * Generic interface to a TTSGenerator 
 * @author welberge
 */
public interface TTSBridge
{
    /**
     * Speaks out the sentence, does the appropriate callbacks, fills out visime,
     * @param text the text or script to speak
     */
    TimingInfo speak(String text);
    
    /**
     * Generates a file containing the spoken sentence, does the apropiate
     * callbacks, fills out visime, phoneme, bookmark and word lists.
     * 
     * @param text
     *            the text or script to speak
     * @throws FileNotFoundException 
     * @throws IOException 
     */
    TimingInfo speakToFile(String text, String filename) throws IOException;
    
    /**
     * Gets the timing of the text, fills out visime, phoneme,bookmark and word
     * lists
     * 
     * @param text
     *            the text or script to get the timing of
     */
    TimingInfo getTiming(String text);    
}
