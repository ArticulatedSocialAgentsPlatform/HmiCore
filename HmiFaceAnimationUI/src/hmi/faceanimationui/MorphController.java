package hmi.faceanimationui;

import hmi.faceanimation.MorphFaceController;

import java.util.Collection;

/**
 * The controller handles input from the viewer and updates the face using the
 * facecontroller
 * 
 * @author hvanwelbergen
 */
public class MorphController {
	private final MorphFaceController model;
	private MorphView mv;

	public MorphController(MorphFaceController mfc) {
		model = mfc;
	}

	public MorphView constructMorphView() {
		mv = new MorphView(this, model.getPossibleFaceMorphTargetNames());
		return mv;
	}

	public void update(Collection<MorphConfiguration> morphs) {
		String morphIds[] = new String[morphs.size()];
		float values[] = new float[morphs.size()];

		int i = 0;
		for (MorphConfiguration mc : morphs) {
			morphIds[i] = mc.getName();
			values[i] = mc.getValue();
			i++;
		}
		model.setMorphTargets(morphIds, values);
	}

	public void setMorphConfigurations(
			Collection<MorphConfiguration> rotations) {
		mv.setMorphConfiguration(rotations);
	}

	public Collection<MorphConfiguration> getMorphConfigurations() {
		return mv.getMorphConfigurations();
	}

	public Collection<MorphConfiguration> getSelectedMorphConfigurations() {
		return mv.getSelectedMorphConfigurations();
	}
}
