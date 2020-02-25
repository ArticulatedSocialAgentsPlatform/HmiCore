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
