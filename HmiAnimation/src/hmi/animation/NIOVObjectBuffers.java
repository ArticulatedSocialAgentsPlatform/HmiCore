package hmi.animation;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Experimental: a group of NIOVObjectBuffers
 * @author Herwin
 *
 */
public class NIOVObjectBuffers
{
    private List<NIOVObjectBuffer> voBuffers = new ArrayList<NIOVObjectBuffer>();
    
    public NIOVObjectBuffers(List<VObject> vos, FloatBuffer rotationBuffer)
    {
        for(VObject vo:vos)
        {
            FloatBuffer fb = rotationBuffer.slice();
            fb.limit(4);
            voBuffers.add(new NIOVObjectBuffer(vo,fb));
            rotationBuffer.position(rotationBuffer.position()+4);            
        }
    }
    
    public void writeBuffers()
    {
        for (NIOVObjectBuffer nvob:voBuffers)
        {
            nvob.writeBuffers();
        }
    }
    
    public void readBuffers()
    {
        for (NIOVObjectBuffer nvob:voBuffers)
        {
            nvob.readBuffers();
        }
    }
}
