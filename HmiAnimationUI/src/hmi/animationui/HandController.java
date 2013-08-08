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
public class HandController {
	private HandAnimator model;
	private JointController jc;

	public HandController(HandAnimator model, JointController jc) {
		this.model = model;
		this.jc = jc;
	}

	public void setJointRotations(HandDOF handConfig, HandSide side) {
		if (side == HandSide.RIGHT) {
			model.setHandDOFRight(handConfig);
			jc.adjustSliderToModel(Arrays.asList(Hanim.RIGHTHAND_JOINTS));
		} else {
			model.setHandDOFLeft(handConfig);
			jc.adjustSliderToModel(Arrays.asList(Hanim.LEFTHAND_JOINTS));
		}
	}

	public HandsView constructHandsView() {
		return new HandsView(this);
	}
}
