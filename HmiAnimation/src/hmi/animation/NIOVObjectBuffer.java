package hmi.animation;

import java.nio.FloatBuffer;

/**
 * Experimental: VObject rotation storage via a NIO buffer
 * @author Herwin
 */
public class NIOVObjectBuffer
{
    private FloatBuffer rotationBuffer = null;
    private VObject joint;
    private float rotation[] = new float[4]; 
    
    public NIOVObjectBuffer(VObject v)
    {
        joint = v;        
    }
    
    public NIOVObjectBuffer(VObject v, FloatBuffer rot)
    {
        joint = v;        
        rotationBuffer = rot;
    }
    
    public void setRotationBuffer(FloatBuffer rot)
    {
        rotationBuffer = rot;        
    }
    
    public void writeBuffers()
    {
        if(rotationBuffer!=null)
        {
            rotationBuffer.get(rotation);
            joint.setRotation(rotation);
        }        
    }
    
    public void readBuffers()
    {
        if(rotationBuffer!=null)
        {
            joint.getRotation(rotation);
            rotationBuffer.put(rotation);
        }
    }
}
