package hmi.tts.sapi5;

import java.io.IOException;
import hmi.tts.TTSBridge;
import hmi.tts.TimingInfo;

/**
 * Microsoft Speech API bridge for SSML
 * @see <a href="http://www.w3.org/TR/speech-synthesis/">SSML Spec</a>
 * @author hvanwelbergen
 *
 */
public class SAPI5SSMLTTSBridge implements TTSBridge
{
    private final SAPI5TTSGenerator ttsGenerator;
    private static final String SSMLOPEN = "<speak version=\"1.0\" xmlns=\"http://www.w3.org/2001/10/synthesis\">";
    private static final String SSMLCLOSE = "</speak>";
    public SAPI5SSMLTTSBridge(SAPI5TTSGenerator ttsGen)
    {
        ttsGenerator = ttsGen;
    }
    
    @Override
    public TimingInfo speak(String text)
    {
        return ttsGenerator.speak(SSMLOPEN+text+SSMLCLOSE);        
    }

    @Override
    public TimingInfo speakToFile(String text, String filename) throws IOException
    {
        return ttsGenerator.speakToFile(SSMLOPEN+text+SSMLCLOSE, filename);        
    }

    @Override
    public TimingInfo getTiming(String text)
    {
        return ttsGenerator.getTiming(SSMLOPEN+text+SSMLCLOSE);        
    }
}
