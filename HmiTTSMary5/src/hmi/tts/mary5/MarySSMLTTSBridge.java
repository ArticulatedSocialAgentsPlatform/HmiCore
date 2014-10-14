package hmi.tts.mary5;

import marytts.datatypes.MaryDataType;

/**
 * TTSBridge for SSML in MaryTTS
 * @see <a href="http://www.w3.org/TR/speech-synthesis/">SSML Spec</a>
 * @author hvanwelbergen
 * 
 */
public class MarySSMLTTSBridge  extends AbstractMaryTTSBridge
{
    public MarySSMLTTSBridge(MaryTTSGenerator ttsGen)
    {
        super(ttsGen,MaryDataType.SSML);
    }

    @Override
    public String wrap(String speechText)
    {
        return ttsGenerator.getSSMLStartTag() + speechText + "</speak>";
    }
}
