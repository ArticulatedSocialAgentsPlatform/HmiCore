/*******************************************************************************
 * Copyright (C) 2009-2020 Human Media Interaction, University of Twente, the Netherlands
 *
 * This file is part of the Articulated Social Agents Platform BML realizer (ASAPRealizer).
 *
 * ASAPRealizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ASAPRealizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ASAPRealizer.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
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
