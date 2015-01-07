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

import hmi.animation.HandAnimator;
import hmi.animation.HandDOF;
import hmi.animation.Hanim;
import hmi.animationui.HandPanel.HandSide;

import java.util.Arrays;

/**
 * The controller handles hand input from the viewer and updates the hand
 * 
 * @author hvanwelbergen
 */
public class HandController
{
    private HandAnimator model;
    private JointController jc;

    public HandController(HandAnimator model)
    {
        this(model,null);
    }
    
    public HandController(HandAnimator model, JointController jc)
    {
        this.model = model;
        this.jc = jc;
    }

    public void setJointRotations(HandDOF handConfig, HandSide side)
    {
        if (side == HandSide.RIGHT)
        {
            model.setHandDOFRight(handConfig);
            if(jc!=null)
            {
                jc.adjustSliderToModel(Arrays.asList(Hanim.RIGHTHAND_JOINTS));
            }
        }
        else
        {
            model.setHandDOFLeft(handConfig);
            if(jc!=null)
            {
                jc.adjustSliderToModel(Arrays.asList(Hanim.LEFTHAND_JOINTS));
            }
        }
    }

    public HandsView constructHandsView()
    {
        return new HandsView(this);
    }
}
