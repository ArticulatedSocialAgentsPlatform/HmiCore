package hmi.tts;

import java.io.IOException;
/**
 * Generic interface to a TTSGenerator through which speech text with BML syncs can be sent 
 * @author welberge
 */
public class BMLTTSBridge implements TTSBridge
{
    private final AbstractTTSGenerator ttsGenerator;
    public BMLTTSBridge(AbstractTTSGenerator ttsGen)
    {
        ttsGenerator = ttsGen;
    }
    
    @Override
    public TimingInfo speak(String text)
    {
        return ttsGenerator.speakBML(text);        
    }

    @Override
    public TimingInfo speakToFile(String text, String filename) throws IOException
    {
        return ttsGenerator.speakBMLToFile(text, filename);        
    }

    @Override
    public TimingInfo getTiming(String text)
    {
        return ttsGenerator.getBMLTiming(text);        
    }    
}
