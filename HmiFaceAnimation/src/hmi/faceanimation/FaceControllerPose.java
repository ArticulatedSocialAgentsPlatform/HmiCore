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
    private Map<String,Float> morphs = new HashMap<String,Float>();
    private MPEG4Configuration mpeg4config = new MPEG4Configuration();
    
    public FaceControllerPose(FaceController fc)
    {
        targetFc = fc;
        for (String morph:fc.getPossibleFaceMorphTargetNames())
        {
            morphs.put(morph, 0f);
        }
    }
    
    public void clear()
    {
        for(Entry<String,Float> entry: morphs.entrySet())
        {
            entry.setValue(0f);
        }
        for(int i=0;i<mpeg4config.getValues().length;i++)
        {
            mpeg4config.setValue(i, 0);
        }
    }
    
    public void toTarget()
    {
        for(Entry<String,Float> entry: morphs.entrySet())
        {
            targetFc.setMorphTargets(new String[]{entry.getKey()}, new float[]{entry.getValue()});
        }
        targetFc.setMPEG4Configuration(mpeg4config);
    }
    
    @Override
    public void copy()
    {
        targetFc.copy();        
    }

    @Override
    public Collection<String> getPossibleFaceMorphTargetNames()
    {
        return morphs.keySet();
    }

    private void addMorphTarget(String target, float value)
    {
        if(morphs.containsKey(target))
        {
            value+=morphs.get(target);
        }
        morphs.put(target, value);
    }
    
    @Override
    public void removeMorphTargets(String[] targets, float[] values)
    {
        for(int i=0;i<targets.length;i++)
        {
            addMorphTarget(targets[i], -values[i]);            
        }        
    }
    
    @Override
    public void addMorphTargets(String[] targets, float[] values)
    {
        for(int i=0;i<targets.length;i++)
        {
            addMorphTarget(targets[i], values[i]);            
        }
    }

    @Override
    public void setMorphTargets(String[] targets, float[] values)
    {
        int i=0;
        for(i=0;i<targets.length;i++)
        {
            morphs.put(targets[i], values[i]);
        }        
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
