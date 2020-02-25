/*******************************************************************************
 * Copyright (C) 2009-2020 Human Media Interaction, University of Twente, the Netherlands
 *
 * This file is part of the Articulated Social Agents Platform BML realizer (ASAPRealizer).
 *
 * ASAPRealizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ASAPRealizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ASAPRealizer.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
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
