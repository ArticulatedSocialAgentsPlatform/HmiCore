package hmi.animationui;

import java.awt.GridLayout;

import hmi.animation.HandDOF;
import hmi.neurophysics.Hand;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lombok.Getter;

/**
 * UI Element for setting hand poses.
 * @author hvanwelbergen
 *
 */
public class HandPanel
{
    @Getter
    private final JPanel panel = new JPanel();
    private final HandController handController;
    private final HandSide handSide;
    
    public enum HandSide{LEFT,RIGHT};
    
    private JSlider DIPIndexFlexionSlider, DIPMiddleFlexionSlider, DIPRingFlexionSlider, DIPPinkyFlexionSlider, IPThumbFlexionSlider,
            MCPIndexFlexionSlider, MCPIndexAbductionSlider, MCPMiddleFlexionSlider, MCPMiddleAbductionSlider, MCPRingFlexionSlider, MCPRingAbductionSlider,
            MCPPinkyFlexionSlider, MCPPinkyAbductionSlider,MCPThumbFlexionSlider, TMCThumbAbductionSlider, TMCThumbFlexionSlider;

    private JSlider createSlider(String name, int min, int max, JPanel parent)
    {
        JPanel p = new JPanel();
        final JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, 0);
        final JLabel sliderLabel = new JLabel("0");
        p.add(new JLabel(name));
        p.add(slider);
        p.add(sliderLabel);

        parent.add(p);

        slider.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                sliderLabel.setText("" + slider.getValue());
                update();
            }
        });
        return slider;
    }

    public HandPanel(String handName, HandController hc, HandSide side)
    {
        this.handController = hc;
        this.handSide = side;
        JLabel label = new JLabel(handName);
        panel.setLayout(new GridLayout(17, 1));

        panel.add(label);
        DIPIndexFlexionSlider = createSlider("PIP index flexion", (int) Math.toDegrees(Hand.getMinimumFingerFlexionPIP()),
                (int) Math.toDegrees(Hand.getMaximumFingerFlexionPIP()), panel);
        DIPMiddleFlexionSlider = createSlider("PIP middle flexion", (int) Math.toDegrees(Hand.getMinimumFingerFlexionPIP()),
                (int) Math.toDegrees(Hand.getMaximumFingerFlexionPIP()), panel);
        DIPRingFlexionSlider = createSlider("PIP ring flexion", (int) Math.toDegrees(Hand.getMinimumFingerFlexionPIP()),
                (int) Math.toDegrees(Hand.getMaximumFingerFlexionPIP()), panel);
        DIPPinkyFlexionSlider = createSlider("PIP pinky flexion", (int) Math.toDegrees(Hand.getMinimumFingerFlexionPIP()),
                (int) Math.toDegrees(Hand.getMaximumFingerFlexionPIP()), panel);

        MCPIndexFlexionSlider = createSlider("MCP index flexion", (int) Math.toDegrees(Hand.getMinimumFingerFlexionMCP()),
                (int) Math.toDegrees(Hand.getMaximumFingerFlexionMCP()), panel);
        MCPIndexAbductionSlider = createSlider("MCP index abduction", (int) Math.toDegrees(Hand.getMinimumFingerAbduction()),
                (int) Math.toDegrees(Hand.getMaximumFingerAbduction()), panel);
        MCPMiddleFlexionSlider = createSlider("MCP middle flexion", (int) Math.toDegrees(Hand.getMinimumFingerFlexionMCP()),
                (int) Math.toDegrees(Hand.getMaximumFingerFlexionMCP()), panel);
        MCPMiddleAbductionSlider = createSlider("MCP middle abduction", (int) Math.toDegrees(Hand.getMinimumFingerAbduction()),
                (int) Math.toDegrees(Hand.getMaximumFingerAbduction()), panel);
        MCPRingFlexionSlider = createSlider("MCP ring flexion", (int) Math.toDegrees(Hand.getMinimumFingerFlexionMCP()),
                (int) Math.toDegrees(Hand.getMaximumFingerFlexionMCP()), panel);
        MCPRingAbductionSlider = createSlider("MCP ring abduction", (int) Math.toDegrees(Hand.getMinimumFingerAbduction()),
                (int) Math.toDegrees(Hand.getMaximumFingerAbduction()), panel);
        MCPPinkyFlexionSlider = createSlider("MCP pinky flexion", (int) Math.toDegrees(Hand.getMinimumFingerFlexionMCP()),
                (int) Math.toDegrees(Hand.getMaximumFingerFlexionMCP()), panel);
        MCPPinkyAbductionSlider = createSlider("MCP pinky abduction", (int) Math.toDegrees(Hand.getMinimumFingerAbduction()),
                (int) Math.toDegrees(Hand.getMaximumFingerAbduction()), panel);
        

        IPThumbFlexionSlider = createSlider("IP thumb flexion", (int) Math.toDegrees(Hand.getMinimumFingerFlexionDIP()),
                (int) Math.toDegrees(Hand.getMaximumFingerFlexionDIP()), panel);
        MCPThumbFlexionSlider = createSlider("MCP thumb flexion", (int) Math.toDegrees(Hand.getMinimumFingerFlexionMCP()),
                (int) Math.toDegrees(Hand.getMaximumFingerFlexionMCP()), panel);
        TMCThumbFlexionSlider = createSlider("TMC thumb flexion", (int) Math.toDegrees(Hand.getMinimumTMCFlexion()),
                (int) Math.toDegrees(Hand.getMaximumTMCFlexion()), panel);
        TMCThumbAbductionSlider = createSlider("TMC thumb abduction", (int) Math.toDegrees(Hand.getMinimumTMCAbduction()),
                (int) Math.toDegrees(Hand.getMaximumTMCAbduction()), panel);
    }

    private void update()
    {   
        handController.setJointRotations(getCurrentHandDOF(), handSide);
    }

    public void setHandDOF(HandDOF h)
    {
        DIPIndexFlexionSlider.setValue((int)Math.toDegrees(h.PIPIndexFlexion));
        DIPMiddleFlexionSlider.setValue((int)Math.toDegrees(h.PIPMiddleFlexion));
        DIPRingFlexionSlider.setValue((int)Math.toDegrees(h.PIPRingFlexion));
        DIPPinkyFlexionSlider.setValue((int)Math.toDegrees(h.PIPRingFlexion));
        MCPIndexFlexionSlider.setValue((int)Math.toDegrees(h.MCPIndexFlexion));
        MCPIndexAbductionSlider.setValue((int)Math.toDegrees(h.MCPIndexAbduction));
        MCPMiddleFlexionSlider.setValue((int)Math.toDegrees(h.MCPMiddleFlexion));
        MCPMiddleAbductionSlider.setValue((int)Math.toDegrees(h.MCPMiddleAbduction));
        MCPRingFlexionSlider.setValue((int)Math.toDegrees(h.MCPRingFlexion));
        MCPRingAbductionSlider.setValue((int)Math.toDegrees(h.MCPRingAbduction));
        MCPPinkyFlexionSlider.setValue((int)Math.toDegrees(h.MCPPinkyFlexion));
        MCPPinkyAbductionSlider.setValue((int)Math.toDegrees(h.MCPPinkyAbduction));
        IPThumbFlexionSlider.setValue((int)Math.toDegrees(h.IPThumbFlexion));
        MCPThumbFlexionSlider.setValue((int)Math.toDegrees(h.MCPThumbFlexion));
        TMCThumbFlexionSlider.setValue((int)Math.toDegrees(h.TMCFlexion));
        TMCThumbAbductionSlider.setValue((int)Math.toDegrees(h.TMCAbduction));
        update();
    }
    
    public HandDOF getCurrentHandDOF()
    {
        HandDOF handDOF = new HandDOF();
        handDOF.PIPIndexFlexion = Math.toRadians(DIPIndexFlexionSlider.getValue());
        handDOF.PIPMiddleFlexion = Math.toRadians(DIPMiddleFlexionSlider.getValue());
        handDOF.PIPRingFlexion = Math.toRadians(DIPRingFlexionSlider.getValue());
        handDOF.PIPPinkyFlexion = Math.toRadians(DIPPinkyFlexionSlider.getValue());
        

        handDOF.MCPIndexAbduction = Math.toRadians(MCPIndexAbductionSlider.getValue());
        handDOF.MCPIndexFlexion = Math.toRadians(MCPIndexFlexionSlider.getValue());
        handDOF.MCPMiddleFlexion = Math.toRadians(MCPMiddleFlexionSlider.getValue());
        handDOF.MCPMiddleAbduction = Math.toRadians(MCPMiddleAbductionSlider.getValue());
        handDOF.MCPRingAbduction = Math.toRadians(MCPRingAbductionSlider.getValue());
        handDOF.MCPRingFlexion = Math.toRadians(MCPRingFlexionSlider.getValue());
        handDOF.MCPPinkyAbduction = Math.toRadians(MCPPinkyAbductionSlider.getValue());
        handDOF.MCPPinkyFlexion = Math.toRadians(MCPPinkyFlexionSlider.getValue());        
        
        handDOF.IPThumbFlexion = Math.toRadians(IPThumbFlexionSlider.getValue());
        handDOF.MCPThumbFlexion = Math.toRadians(MCPThumbFlexionSlider.getValue());
        handDOF.TMCAbduction = Math.toRadians(TMCThumbAbductionSlider.getValue());
        handDOF.TMCFlexion = Math.toRadians(TMCThumbFlexionSlider.getValue());
        return handDOF;
    }
}
