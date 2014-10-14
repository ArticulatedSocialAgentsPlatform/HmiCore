package hmi.tts.mary5;

import java.io.IOException;

import marytts.datatypes.MaryDataType;
import hmi.tts.TTSBridge;
import hmi.tts.TimingInfo;

/**
 * Shared implementation part of all MaryTTS bridges 
 * @author hvanwelbergen
 */
abstract class AbstractMaryTTSBridge implements TTSBridge
{
    protected final MaryTTSGenerator ttsGenerator;
    private final MaryDataType inputType;
    public AbstractMaryTTSBridge(MaryTTSGenerator ttsGen, MaryDataType input)
    {
        ttsGenerator = ttsGen;
        inputType = input;
    }
    
    @Override
    public TimingInfo speak(String text)
    {
        return ttsGenerator.speak(wrap(text),inputType);
    }

    @Override
    public TimingInfo speakToFile(String text, String filename) throws IOException
    {
        TimingInfo ti = getTiming(text);
        ttsGenerator.speakToFile(wrap(text),filename,inputType);
        return ti;
    }

    @Override
    public TimingInfo getTiming(String text)
    {
        return ttsGenerator.getTiming(wrap(text),inputType);        
    }
    
    /**
     * Wraps speech text into proper start/end tags for this input format
     */
    public abstract String wrap(String speechText);
}
