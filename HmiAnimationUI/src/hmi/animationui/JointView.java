package hmi.animationui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import lombok.Getter;

/**
 * A user interface to set and update joint rotations
 * @author hvanwelbergen
 */
public class JointView
{
    private final JointController controller;
    
    @Getter
    private JPanel panel = new JPanel();
    private List<JointRotationPanel> rotPanels = new ArrayList<>();
    
    public JointView(JointController controller, List<String> joints)
    {
        this.controller = controller;
        
    }    
    
    public void update()
    {
        List<JointRotationConfiguration> jrcList = new ArrayList<>();
        for(JointRotationPanel rp: rotPanels)
        {
            jrcList.add(rp.getRotationConfiguration());
        }        
        controller.setJointRotations(jrcList);
    }    
}
