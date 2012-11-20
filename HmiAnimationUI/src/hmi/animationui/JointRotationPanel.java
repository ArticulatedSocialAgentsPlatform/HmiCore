package hmi.animationui;

import hmi.math.Quat4f;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lombok.Getter;

/**
 * UI element for the rotation of a single joint
 * @author hvanwelbergen
 */
public class JointRotationPanel
{
    @Getter
    private final JPanel panel = new JPanel();
    private final String jointName;
    private final JSlider pitchSlider;
    private final JSlider yawSlider;
    private final JSlider rollSlider;
    private final JointView jointView;

    private void setupSlider(JSlider s)
    {
        panel.add(s);
        s.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                jointView.update();
            }
        });
    }
    
    public JointRotationPanel(String jointName, JointView jointView)
    {
        this.jointName = jointName;
        this.jointView = jointView;
        JLabel label = new JLabel(jointName);
        panel.add(label);

        pitchSlider = new JSlider(JSlider.HORIZONTAL, -180, 180);
        yawSlider = new JSlider(JSlider.HORIZONTAL, -180, 180);
        rollSlider = new JSlider(JSlider.HORIZONTAL, -180, 180);
        
        setupSlider(pitchSlider);
        setupSlider(yawSlider);
        setupSlider(rollSlider);
    }

    public JointRotationConfiguration getRotationConfiguration()
    {
        float q[] = Quat4f.getQuat4f();
        Quat4f.setFromRollPitchYawDegrees(q, rollSlider.getValue(), pitchSlider.getValue(), yawSlider.getValue());
        return new JointRotationConfiguration(jointName, q);
    }
}
