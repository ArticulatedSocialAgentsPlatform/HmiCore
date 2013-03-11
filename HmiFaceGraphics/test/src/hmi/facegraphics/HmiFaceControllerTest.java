package hmi.facegraphics;

import hmi.graphics.opengl.scenegraph.GLScene;

import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.any;

/**
 * unit tests for the HmiFaceController
 * @author Herwin
 *
 */
public class HmiFaceControllerTest
{
    private GLScene mockScene = mock(GLScene.class);
    private GLHead mockHead = mock(GLHead.class);
    
    @Test
    public void test()
    {
        HMIFaceController fc = new HMIFaceController(mockScene, mockHead);
        String targetNames[]={"x","y","z"};
        float weights[]={0.1f,0.2f,0.3f};
        fc.addMorphTargets(targetNames, weights);
        fc.copy();
        verify(mockScene).setMorphTargets(any(String[].class),any(float[].class));
    }
}
