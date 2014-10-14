package hmi.tts.mary5;

import marytts.datatypes.MaryDataType;

/**
 * TTSBridge for RAWMARYXML @see <a href="http://mary.dfki.de/documentation/maryxml">RAWMARYXML</a>
 * @author hvanwelbergen
 *
 */
public class MaryXMLTTSBridge extends AbstractMaryTTSBridge
{
    public MaryXMLTTSBridge(MaryTTSGenerator ttsGen)
    {
        super(ttsGen,MaryDataType.RAWMARYXML);
    }

    @Override
    public String wrap(String speechText)
    {
        return ttsGenerator.getMaryXMLStartTag()+speechText+"</maryxml>";
    }

}
