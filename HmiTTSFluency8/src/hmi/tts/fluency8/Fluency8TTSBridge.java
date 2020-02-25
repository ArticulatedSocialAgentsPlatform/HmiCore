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
package hmi.tts.fluency8;

import java.io.IOException;
import hmi.tts.TTSBridge;
import hmi.tts.TimingInfo;

/**
 * Fluency bridge for fluency's own format
 * @see <a href="http://www.fluency.nl/help/play.htm">Fluency website</a>
 * note that fluency format accepts only numbers as bookmarkid! So we will strip the other characters from the id, which is a bit of a hack...
 * @author Dennis Reidsma
 *
 */
public class Fluency8TTSBridge implements TTSBridge
{
    private final Fluency8TTSGenerator ttsGenerator;
    public Fluency8TTSBridge(Fluency8TTSGenerator ttsGen)
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