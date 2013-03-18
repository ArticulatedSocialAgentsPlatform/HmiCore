package hmi.animationui;

import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import lombok.Getter;

/**
 * A user interface to set and update ik positions
 * 
 * @author hvanwelbergen
 */
public class IKView
{
    @Getter
    private JPanel panel = new JPanel();
    
    public IKView(List<AnalyticalIKController> controllers)
    {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (AnalyticalIKController c:controllers)
        {
            IKPanel ikPanel = new IKPanel(c.getId(), c, c.getStartPos(), c.getStartSwivel());
            panel.add(ikPanel.getPanel());            
        }
        panel.add(Box.createVerticalGlue());
    }    
}
