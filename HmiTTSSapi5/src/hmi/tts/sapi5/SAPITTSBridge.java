package hmi.tts.sapi5;

import java.io.IOException;
import hmi.tts.TTSBridge;
import hmi.tts.TimingInfo;

/**
 * Microsoft Speech API implementation of the TTSBridge, uses SAPI XML.
 * @author hvanwelbergen
 *
 */
public class SAPITTSBridge implements TTSBridge
{
    private final SAPI5TTSGenerator ttsGenerator;
    public SAPITTSBridge(SAPI5TTSGenerator ttsGen)
    {
        ttsGenerator = ttsGen;
    }
    
    @Override
    public TimingInfo speak(String text)
    {
        return ttsGenerator.speak(text);        
    }

    @Override
    public TimingInfo speakToFile(String text, String filename) throws IOException
    {
        return ttsGenerator.speakToFile(text, filename);        
    }

    @Override
    public TimingInfo getTiming(String text)
    {
        
        return ttsGenerator.getTiming(text);        
    }
}
