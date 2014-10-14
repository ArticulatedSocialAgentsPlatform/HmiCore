package hmi.tts.mary5;

import marytts.datatypes.MaryDataType;

/**
 * TTSBridge for WORDS in MaryTTS
 * @author hvanwelbergen
 *
 */
public class MaryWordsTTSBridge extends AbstractMaryTTSBridge
{
    public MaryWordsTTSBridge(MaryTTSGenerator ttsGen)
    {
        super(ttsGen,MaryDataType.WORDS);
    }

    @Override
    public String wrap(String speechText)
    {
        return ttsGenerator.getMaryXMLStartTag()+speechText+"</maryxml>";
    }

}
