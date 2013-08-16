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
