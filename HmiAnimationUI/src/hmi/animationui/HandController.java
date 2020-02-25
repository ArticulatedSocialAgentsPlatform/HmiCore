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
