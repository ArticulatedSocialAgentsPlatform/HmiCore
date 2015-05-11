/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2015 University of Twente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
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
