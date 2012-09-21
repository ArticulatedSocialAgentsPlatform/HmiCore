package hmi.faceanimation;

import hmi.faceanimation.model.MPEG4Configuration;

/**
 * Dummy MPEG4 face controller that does nothing
 * @author hvanwelbergen
 */
public class NullMPEG4FaceController implements MPEG4FaceController
{

    @Override
    public void setMPEG4Configuration(MPEG4Configuration config)
    {
    }

    @Override
    public void addMPEG4Configuration(MPEG4Configuration config)
    {
    }

    @Override
    public void removeMPEG4Configuration(MPEG4Configuration config)
    {
    }

    @Override
    public void copy()
    {
    }
}
