package hmi.faceanimationui;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lombok.Getter;

/**
 * UI element for the deformation of a single morph target
 * @author hvanwelbergen
 */
public class MorphPanel
{
    @Getter
    private final JPanel panel = new JPanel();
    private final String morphName;
    private final JSlider morphSlider;
    
    public MorphPanel(String morphName, final MorphView morphView)
    {
        this.morphName = morphName;
        morphSlider = new JSlider(JSlider.HORIZONTAL, -100, 100, 0);
        JPanel sliderPanel = new JPanel();
        final JLabel sliderLabel = new JLabel("0");
        sliderLabel.setPreferredSize(new Dimension(30, 20));
        sliderPanel.add(sliderLabel);
        sliderPanel.add(morphSlider);
        panel.add(sliderPanel);
        morphSlider.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                morphView.update();
                sliderLabel.setText(""+morphSlider.getValue());
            }
        });        
    }
    
    public MorphConfiguration getMorphConfiguration()
    {
        return new MorphConfiguration(morphName, (float)morphSlider.getValue()/100.0f);
    }
}
