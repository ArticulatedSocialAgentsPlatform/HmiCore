package hmi.animationui;

import hmi.animation.Torso;

/**
 * The controller handles torso rotation input from the viewer and updates the torso
 * @author hvanwelbergen
 */
public class TorsoController
{
    private final Torso torso;
    public TorsoController(Torso torso)
    {
        this.torso = torso;
    }
    
    public TorsoView constructTorsoView()
    {
        return new TorsoView(this);
    }
    
    public void setTorsoRotation(float pitch, float yaw, float roll)
    {
        torso.setTorsoRollPitchYawDegrees(roll, pitch, yaw);
    }
}
