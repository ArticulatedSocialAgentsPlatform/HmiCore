package hmi.faceanimationui;

import hmi.faceanimation.MorphFaceController;

import java.util.Collection;

/**
 * The controller handles input from the viewer and updates the face using the facecontroller
 * @author hvanwelbergen
 */
public class MorphController
{
    private final MorphFaceController model;

    public MorphController(MorphFaceController mfc)
    {
        model = mfc;
    }

    public MorphView constructMorphView()
    {
        return new MorphView(this, model.getPossibleFaceMorphTargetNames());
    }
    
    public void update(Collection<MorphConfiguration> morphs)
    {
        String morphIds[] = new String[morphs.size()];
        float values[] = new float[morphs.size()];

        int i = 0;
        for (MorphConfiguration mc : morphs)
        {
            morphIds[i] = mc.getName();
            values[i] = mc.getValue();
            i++;
        }
        model.setMorphTargets(morphIds,values);
    }
}
