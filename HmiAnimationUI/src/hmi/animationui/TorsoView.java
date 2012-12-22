package hmi.animationui;

import javax.swing.JPanel;

import lombok.Getter;

/**
 * A user interface to set and update the torso rotation
 * @author hvanwelbergen
 */
public class TorsoView implements Viewer
{
    private final TorsoController controller;
    private final JointRotationPanel torsoRotation;
    
    @Getter
    private JPanel panel;
    
    public TorsoView(TorsoController c)
    {
        controller = c;
        torsoRotation = new JointRotationPanel("Torso", this);        
        panel = torsoRotation.getPanel();        
    }
    
    public void update()
    {
        controller.setTorsoRotation(torsoRotation.getPitch(), torsoRotation.getYaw(), torsoRotation.getRoll());
    }
}
