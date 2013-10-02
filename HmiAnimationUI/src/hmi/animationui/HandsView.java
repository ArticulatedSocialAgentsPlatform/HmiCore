package hmi.animationui;

import hmi.animation.HandDOF;
import hmi.animationui.HandPanel.HandSide;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import lombok.Getter;

public class HandsView
{
    @Getter
    private JPanel panel = new JPanel();
    private final HandPanel leftHandPanel;
    private final HandPanel rightHandPanel;

    public HandsView(HandController hc)
    {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        rightHandPanel = new HandPanel("Right hand", hc, HandSide.RIGHT);
        panel.add(rightHandPanel.getPanel());

        leftHandPanel = new HandPanel("Left hand", hc, HandSide.LEFT);
        panel.add(leftHandPanel.getPanel());
    }

    public HandDOF getLeftHandDOF()
    {
        return leftHandPanel.getCurrentHandDOF();
    }
    
    public HandDOF getRightHandDOF()
    {
        return rightHandPanel.getCurrentHandDOF();
    }
    
    public void setRightHandDOF(HandDOF h)
    {
        rightHandPanel.setHandDOF(h);
    }
    
    public void setLeftHandDOF(HandDOF h)
    {
        leftHandPanel.setHandDOF(h);
    }
}
