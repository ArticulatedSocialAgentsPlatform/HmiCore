package hmi.faceanimation;

import hmi.faceanimation.model.MPEG4Configuration;

/**
 * Controls the face using MPEG4
 * @author hvanwelbergen
 *
 */
public interface MPEG4FaceController
{
    /**
     * Directly set a FAP configuration on the face. FAPs that are assigned NULL
     * values will not be modified at all. All other FAPs will be completely
     * overwritten with the new configuration, i.e., no blending is done.
     */
    void setMPEG4Configuration(MPEG4Configuration config);

    void addMPEG4Configuration(MPEG4Configuration config);

    void removeMPEG4Configuration(MPEG4Configuration config);
    
    /**
     * Do actually apply the current MPEG4 configurations to the face
     */
    void copy();
}
