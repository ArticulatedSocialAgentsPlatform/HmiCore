package hmi.animationui;

import hmi.animation.HandAnimator;
import hmi.animation.HandDOF;
import hmi.animationui.HandPanel.HandSide;

public class HandController
{
    private HandAnimator model;
    public HandController(HandAnimator model)
    {
        this.model = model;
    }
    
    
    public void setJointRotations(HandDOF handConfig, HandSide side)
    {
        if(side ==HandSide.RIGHT)
        {
            model.setHandDOFRight(handConfig);
        }
        else
        {
            model.setHandDOFLeft(handConfig);
        }
    }
    
    public HandsView constructHandsView()
    {
        return new HandsView(this);
    }
}
