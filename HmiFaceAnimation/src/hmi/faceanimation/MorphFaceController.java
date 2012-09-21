package hmi.faceanimation;

import java.util.Collection;

/**
 * Controls the face using morph targets
 * @author hvanwelbergen
 *
 */
public interface MorphFaceController
{
    void setMorphTargets(String[] targetNames, float[] weights);

    void addMorphTargets(String[] targetNames, float[] weights);

    void removeMorphTargets(String[] targetNames, float[] weights);

    Collection<String> getPossibleFaceMorphTargetNames();
    
    /**
     * Do actually apply the current morphs to the face
     */
    void copy();
}
