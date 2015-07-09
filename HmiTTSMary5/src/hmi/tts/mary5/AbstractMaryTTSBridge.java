/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2015 University of Twente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package hmi.tts.mary5;

import hmi.tts.TTSBridge;
import hmi.tts.TTSException;

import java.io.IOException;

import marytts.datatypes.MaryDataType;

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
    public MaryProsody speak(String text)throws TTSException
    {
        return ttsGenerator.speak(wrap(text),inputType);
    }

    @Override
    public MaryProsody speakToFile(String text, String filename) throws IOException, TTSException
    {
        return ttsGenerator.speakToFile(wrap(text),filename,inputType);        
    }

    @Override
    public MaryProsody getTiming(String text) throws TTSException
    {
        return ttsGenerator.getTiming(wrap(text),inputType);        
    }
    
    /**
     * Wraps speech text into proper start/end tags for this input format
     */
    public abstract String wrap(String speechText);
}
