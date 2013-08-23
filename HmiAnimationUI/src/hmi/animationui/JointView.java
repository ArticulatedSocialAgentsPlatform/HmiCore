package hmi.animationui;

import hmi.animation.VJoint;
import hmi.math.Quat4f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import lombok.Getter;

/**
 * A user interface to set and update joint rotations
 * 
 * @author hvanwelbergen
 */
public class JointView
{
    private final RotationsController controller;

    @Getter
    private JPanel panel = new JPanel();

    @Getter
    private Map<String, JointRotationPanel> rotationPanels = new HashMap<>();

    public JointView(RotationsController controller, Collection<String> joints)
    {
        this.controller = controller;
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (String vj : joints)
        {
            JointRotationPanel rp = new JointRotationPanel(vj, this);
            rotationPanels.put(vj, rp);
            panel.add(rp.getPanel());
        }
        panel.add(Box.createVerticalGlue());
    }

    public Collection<JointRotationConfiguration> getJointRotationConfigurations()
    {
        Collection<JointRotationConfiguration> rotationConfigurations = new ArrayList<JointRotationConfiguration>();
        for (JointRotationPanel rp : rotationPanels.values())
        {
            rotationConfigurations.add(rp.getRotationConfiguration());
        }
        return rotationConfigurations;
    }

    public Collection<JointRotationConfiguration> getSelectedJointRotationConfigurations()
    {
        Collection<JointRotationConfiguration> rotationConfigurations = new ArrayList<JointRotationConfiguration>();
        for (JointRotationPanel rp : rotationPanels.values())
        {
            if (rp.useInKeyFrame())
            {
                rotationConfigurations.add(rp.getRotationConfiguration());
            }
        }
        return rotationConfigurations;
    }

    public void setJointRotationConfiguration(Collection<JointRotationConfiguration> rotationConfigurations)
    {
        reset();
        for (JointRotationConfiguration j : rotationConfigurations)
        {
            JointRotationPanel rp = rotationPanels.get(j.getJointName());
            if (rp != null)
            {
                rp.setJointRotationConfiguration(j);
            }
        }
    }

    public void update()
    {
        List<JointRotationConfiguration> jrcList = new ArrayList<>();
        for (JointRotationPanel rp : rotationPanels.values())
        {
            jrcList.add(rp.getRotationConfiguration());
        }
        controller.setJointRotations(jrcList);
    }

    public void reset()
    {
        for (JointRotationPanel j : rotationPanels.values())
        {
            j.reset();
        }
    }

    /**
     * Sets the values of all sliders for the joints in <i>joints</i> to the
     * value of the corresponding joint according to the current model pose.
     * 
     * @param joints
     */
    public void adjustSliderToModel(VJoint model, Collection<String> joints)
    {
        float q[] = Quat4f.getQuat4f();        
        for (String j : joints)
        {
            JointRotationPanel rp = rotationPanels.get(j);
            if (rp != null)
            {
                VJoint p = model.getPart(rp.getRotationConfiguration().getJointName());
                p.getRotation(q);
                float rpyDeg[] = new float[3];
                Quat4f.getRollPitchYaw(q, rpyDeg);
                for (int i = 0; i < rpyDeg.length; i++)
                {
                    rpyDeg[i] = (float) Math.toDegrees(rpyDeg[i]);
                }
                rp.setJointRotationConfiguration(new JointRotationConfiguration(rp.getRotationConfiguration().getJointName(), q, rpyDeg));
            }
        }
    }

}
