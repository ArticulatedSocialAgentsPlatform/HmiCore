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
package hmi.faceanimation;

import java.util.HashMap;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableMap;

/**
 * Keeps track of morph target values
 * @author hvanwelbergen
 *
 */
@ThreadSafe
public class MorphTargetHandler
{
    /**
     * The set of morph targets to be set by doMorph, maintained through addMorphTargets and removeMorphTargets
     */
    private HashMap<String, Float> desiredMorphTargets = new HashMap<String, Float>();

    /**
     * Get an immutable copy of the current desired morph target configuration
     */
    public synchronized ImmutableMap<String, Float> getDesiredMorphTargets()
    {
        return ImmutableMap.copyOf(desiredMorphTargets);
    }
    
    /** Add given weights for given morph targets to the list of desired targets */
    public synchronized void addMorphTargets(String[] targetNames, float[] weights)
    {
        float w = 0;
        for (int i = 0; i < targetNames.length; i++)
        {
            w = weights[i];
            Float fl = desiredMorphTargets.get(targetNames[i]);
            if (fl != null)
            {
                w += fl.floatValue();
            }
            if (w == 0)
            {
                desiredMorphTargets.remove(targetNames[i]);
            }
            else
            {
                desiredMorphTargets.put(targetNames[i], new Float(w));
            }
        }
    }

    public synchronized void setMorphTargets(String[] targetNames, float[] weights)
    {
        for (int i = 0; i < targetNames.length; i++)
        {
            desiredMorphTargets.put(targetNames[i], new Float(weights[i]));
        }
    }

    /** Remove given weights for given morph targets from the list of desired targets */
    public synchronized void removeMorphTargets(String[] targetNames, float[] weights)
    {
        float w = 0;
        for (int i = 0; i < targetNames.length; i++)
        {
            w = -weights[i];
            Float fl = desiredMorphTargets.get(targetNames[i]);
            if (fl != null)
            {
                w += fl.floatValue();
            }
            if (w == 0)
            {
                desiredMorphTargets.remove(targetNames[i]);
            }
            else
            {
                desiredMorphTargets.put(targetNames[i], new Float(w));
            }
        }
    }
    
    public synchronized float getCurrentWeight(String targetName)
    {
        if(desiredMorphTargets.containsKey(targetName))
        {
            return desiredMorphTargets.get(targetName);
        }
        return 0;
    }
}
