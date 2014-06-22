package hmi.tts.mary;

import marytts.datatypes.MaryDataType;

/**
 * TTSBridge for ALLOPHONES in MaryTTS 
 * @author hvanwelbergen
 * 
 */
public class MaryAllophonesTTSBridge extends AbstractMaryTTSBridge
{
    public MaryAllophonesTTSBridge(MaryTTSGenerator ttsGen)
    {
        super(ttsGen,MaryDataType.ALLOPHONES);
    }

    @Override
    public String wrap(String speechText)
    {
        return ttsGenerator.getMaryXMLStartTag()+speechText+"</maryxml>";
    }

}
