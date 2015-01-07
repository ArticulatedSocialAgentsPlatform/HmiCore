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
