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
