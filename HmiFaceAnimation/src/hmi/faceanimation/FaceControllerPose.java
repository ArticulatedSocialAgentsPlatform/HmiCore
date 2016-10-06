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

import hmi.faceanimation.model.MPEG4Configuration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Keeps track of a specific face controller state, set by the normal FaceController functionality.
 * This state can then be put onto a target facecontroller using toTarget.
 * @author Herwin
 */
public class FaceControllerPose implements FaceController
{
    private final FaceController targetFc;
    private Map<String, Float> morphs = new HashMap<String, Float>();
    private MPEG4Configuration mpeg4config = new MPEG4Configuration();

    public FaceControllerPose(FaceController fc)
    {
        targetFc = fc;
        for (String morph : fc.getPossibleFaceMorphTargetNames())
        {
            morphs.put(morph, 0f);
        }
    }

    public void clear()
    {
        for (Entry<String, Float> entry : morphs.entrySet())
        {
            entry.setValue(0f);
        }
        for (int i = 0; i < mpeg4config.getValues().length; i++)
        {
            mpeg4config.setValue(i, 0);
        }
    }

    public synchronized void toTarget()
    {
        synchronized (targetFc)
        {
            for (Entry<String, Float> entry : morphs.entrySet())
            {
                targetFc.setMorphTargets(new String[] { entry.getKey() }, new float[] { entry.getValue() });
            }
            targetFc.setMPEG4Configuration(mpeg4config);
        }
    }
    public synchronized void toTargetAdditive()
    {
        synchronized (targetFc)
        {
            for (Entry<String, Float> entry : morphs.entrySet())
            {
                targetFc.addMorphTargets(new String[] { entry.getKey() }, new float[] { entry.getValue() });
            }
            targetFc.addMPEG4Configuration(mpeg4config);
        }
    }

    @Override
    public synchronized void copy()
    {

    }

    @Override
    public Collection<String> getPossibleFaceMorphTargetNames()
    {
        return morphs.keySet();
    }

    private void addMorphTarget(String target, float value)
    {
        if (morphs.containsKey(target))
        {
            value += morphs.get(target);
        }
        morphs.put(target, value);
    }

    @Override
    public void removeMorphTargets(String[] targets, float[] values)
    {
        for (int i = 0; i < targets.length; i++)
        {
            addMorphTarget(targets[i], -values[i]);
        }
    }

    @Override
    public void addMorphTargets(String[] targets, float[] values)
    {
        for (int i = 0; i < targets.length; i++)
        {
            addMorphTarget(targets[i], values[i]);
        }
    }

    @Override
    public void setMorphTargets(String[] targets, float[] values)
    {
        for (int i = 0; i < targets.length; i++)
        {
            morphs.put(targets[i], values[i]);
        }
    }

    @Override
    public float getCurrentWeight(String targetName)
    {
        return morphs.get(targetName);
    }
    
    @Override
    public void addMPEG4Configuration(MPEG4Configuration mpeg4Config)
    {
        mpeg4config.addValues(mpeg4Config);
    }

    @Override
    public void removeMPEG4Configuration(MPEG4Configuration mpeg4Config)
    {
        mpeg4config.removeValues(mpeg4Config);
    }

    @Override
    public void setMPEG4Configuration(MPEG4Configuration mpeg4Config)
    {
        mpeg4config.setValues(mpeg4Config.getValues());
    }

   
}
