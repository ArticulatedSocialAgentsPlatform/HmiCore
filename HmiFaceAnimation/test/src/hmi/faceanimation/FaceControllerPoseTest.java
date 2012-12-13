package hmi.faceanimation;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import hmi.faceanimation.model.MPEG4Configuration;

import org.junit.Test;
/**
 * Unit tests for the FaceControllerPose
 * @author Herwin
 * 
 */
public class FaceControllerPoseTest
{
    private FaceController mockFaceController = mock(FaceController.class);

    @Test
    public void testEmptyToTarget()
    {
        FaceControllerPose pose = new FaceControllerPose(mockFaceController);
        pose.toTarget();
        verify(mockFaceController, times(0)).setMorphTargets(any(String[].class), any(float[].class));
        verify(mockFaceController).setMPEG4Configuration(any(MPEG4Configuration.class));
    }
}
