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
package hmi.tts.mary5;

import hmi.tts.Bookmark;
import hmi.tts.Prosody;
import hmi.tts.TTSTiming;
import hmi.tts.TimingInfo;
import hmi.tts.Visime;
import hmi.tts.WordDescription;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;

/**
 * TTSTiming+f0 and RMS Energy trajectories
 * @author herwinvw
 */
public class MaryProsody implements TTSTiming
{
    public MaryProsody(TimingInfo info)
    {
        ti = info;
    }

    private final TimingInfo ti;

    @Getter
    private double f0[];

    @Getter
    private double rmsEnergy[];

    public void setRMSEnergy(double rmsEnergy[])
    {
        if(f0!=null && f0.length>0)
        {
            this.rmsEnergy = new double[f0.length];
            Arrays.fill(this.rmsEnergy, rmsEnergy[rmsEnergy.length-1]);
            System.arraycopy(rmsEnergy, 0, this.rmsEnergy, 0, Math.min(f0.length,rmsEnergy.length));
        }
        else
        {
            this.rmsEnergy = Arrays.copyOf(rmsEnergy, rmsEnergy.length);
        }
    }

    public void setF0(double f0[])
    {
        this.f0 = Arrays.copyOf(f0, f0.length);
    }

    @Override
    public List<WordDescription> getWordDescriptions()
    {
        return ti.getWordDescriptions();
    }

    @Override
    public List<Bookmark> getBookmarks()
    {
        return ti.getBookmarks();
    }

    @Override
    public Bookmark getBookmark(String id)
    {
        return ti.getBookmark(id);
    }

    @Override
    public double getDuration()
    {
        return ti.getDuration();
    }

    @Override
    public List<Visime> getVisimes()
    {
        return ti.getVisimes();
    }

    @Override 
    public Prosody getProsody()
    {
        return new Prosody(f0, rmsEnergy, getDuration()/getF0().length);
    }
}
