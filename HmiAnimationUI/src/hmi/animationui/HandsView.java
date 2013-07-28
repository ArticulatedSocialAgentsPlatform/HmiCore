package hmi.animationui;

import hmi.animationui.HandPanel.HandSide;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import lombok.Getter;

public class HandsView
{
    @Getter
    private JPanel panel = new JPanel();

    public HandsView(HandController hc)
    {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        HandPanel hpr = new HandPanel("Right hand", hc, HandSide.RIGHT);
        panel.add(hpr.getPanel());

        HandPanel hpl = new HandPanel("Left hand", hc, HandSide.LEFT);
        panel.add(hpl.getPanel());
    }

}
