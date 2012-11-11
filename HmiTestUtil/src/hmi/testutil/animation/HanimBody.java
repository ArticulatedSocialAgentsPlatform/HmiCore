package hmi.testutil.animation;

import hmi.animation.Hanim;
import hmi.animation.VJoint;

/**
 * Generates stub Hanim VJoint hierarchies. 
 * @author welberge
 */
public final class HanimBody
{
    private HanimBody(){}
    
    public static VJoint getLOA1HanimBodyWithEyes()
    {
        VJoint vj = getLOA1HanimBody();
        VJoint skull = vj.getPart(Hanim.skullbase);
        VJoint vEyeLeft = new VJoint();
        VJoint vEyeRight = new VJoint();
        vEyeRight.setSid(Hanim.r_eyeball_joint);
        vEyeLeft.setSid(Hanim.l_eyeball_joint);
        skull.addChild(vEyeLeft);
        skull.addChild(vEyeRight);
        return vj;        
    }
    
    public static VJoint getLOA2HanimBody()
    {
        VJoint HumanoidRoot = new VJoint();
        HumanoidRoot.setSid(Hanim.HumanoidRoot);
        
        VJoint sacroiliac = new VJoint();
        sacroiliac.setSid(Hanim.sacroiliac);
        HumanoidRoot.addChild(sacroiliac);
        
        VJoint l_hip = new VJoint();
        l_hip.setSid(Hanim.l_hip);
        sacroiliac.addChild(l_hip);
        
        VJoint l_knee = new VJoint();
        l_knee.setSid(Hanim.l_knee);
        l_hip.addChild(l_knee);
        
        VJoint l_ankle = new VJoint();
        l_ankle.setSid(Hanim.l_ankle);
        l_knee.addChild(l_ankle);
        
        VJoint l_subtalar = new VJoint();
        l_subtalar.setSid(Hanim.l_subtalar);
        l_ankle.addChild(l_ankle);
        
        VJoint l_midtarsal = new VJoint();
        l_midtarsal.setSid(Hanim.l_midtarsal);
        l_subtalar.addChild(l_midtarsal);
        
        VJoint l_metatarsal = new VJoint();
        l_metatarsal.setSid(Hanim.l_metatarsal);
        l_subtalar.addChild(l_metatarsal);
        
        VJoint r_hip = new VJoint();
        r_hip.setSid(Hanim.r_hip);
        sacroiliac.addChild(r_hip);
        
        VJoint r_knee = new VJoint();
        r_knee.setSid(Hanim.r_knee);
        r_hip.addChild(r_knee);
        
        VJoint r_ankle = new VJoint();
        r_ankle.setSid(Hanim.r_ankle);
        r_knee.addChild(r_ankle);
        
        VJoint r_subtalar = new VJoint();
        r_subtalar.setSid(Hanim.r_subtalar);
        r_ankle.addChild(r_subtalar);
        
        VJoint r_midtarsal = new VJoint();
        r_midtarsal.setSid(Hanim.r_midtarsal);
        r_subtalar.addChild(r_midtarsal);
        
        VJoint r_metatarsal = new VJoint();
        r_metatarsal.setSid(Hanim.r_metatarsal);
        r_midtarsal.addChild(r_metatarsal);
        
        VJoint vl5 = new VJoint();
        vl5.setSid(Hanim.vl5);
        HumanoidRoot.addChild(vl5);
        
        VJoint vl3 = new VJoint();
        vl3.setSid(Hanim.vl3);
        vl5.addChild(vl3);
        
        VJoint vl1 = new VJoint();
        vl1.setSid(Hanim.vl1);
        vl3.addChild(vl1);
        
        VJoint vt10 = new VJoint();
        vt10.setSid(Hanim.vt10);
        vl1.addChild(vt10);
        
        VJoint vt6 = new VJoint();
        vt6.setSid(Hanim.vt6);
        vt10.addChild(vt6);
        
        VJoint vt1 = new VJoint();
        vt1.setSid(Hanim.vt1);
        vt6.addChild(vt1);
        
        VJoint vc4 = new VJoint();
        vc4.setSid(Hanim.vc4);
        vt1.addChild(vc4);
        
        VJoint vc2 = new VJoint();
        vc2.setSid(Hanim.vc2);
        vc4.addChild(vc2);
        
        VJoint skullbase = new VJoint();
        skullbase.setSid(Hanim.skullbase);
        vc4.addChild(skullbase);
        
        VJoint l_sternoclavicular = new VJoint();
        l_sternoclavicular.setSid(Hanim.l_sternoclavicular);
        vt1.addChild(l_sternoclavicular);
        
        VJoint l_acromioclavicular = new VJoint();
        l_acromioclavicular.setSid(Hanim.l_acromioclavicular);
        l_sternoclavicular.addChild(l_acromioclavicular);
        
        VJoint l_shoulder = new VJoint();
        l_shoulder.setSid(Hanim.l_shoulder);
        l_acromioclavicular.addChild(l_shoulder);
        
        VJoint l_elbow = new VJoint();
        l_elbow.setSid(Hanim.l_elbow);
        l_shoulder.addChild(l_elbow);
        
        VJoint l_wrist = new VJoint();
        l_wrist.setSid(Hanim.l_wrist);
        l_elbow.addChild(l_wrist);
        
        VJoint l_thumb1 = new VJoint();
        l_thumb1.setSid(Hanim.l_thumb1);
        l_wrist.addChild(l_thumb1);
        
        VJoint l_thumb2 = new VJoint();
        l_thumb2.setSid(Hanim.l_thumb2);
        l_thumb1.addChild(l_thumb2);
        
        VJoint l_thumb3 = new VJoint();
        l_thumb3.setSid(Hanim.l_thumb3);
        l_thumb2.addChild(l_thumb3);
        
        VJoint l_index0 = new VJoint();
        l_index0.setSid(Hanim.l_index0);
        l_wrist.addChild(l_index0);
        
        VJoint l_index1 = new VJoint();
        l_index1.setSid(Hanim.l_index1);
        l_index0.addChild(l_index1);
        
        VJoint l_index2 = new VJoint();
        l_index2.setSid(Hanim.l_index2);
        l_index1.addChild(l_index2);
        
        VJoint l_index3 = new VJoint();
        l_index3.setSid(Hanim.l_index3);
        l_index2.addChild(l_index3);
        
        VJoint l_middle0 = new VJoint();
        l_middle0.setSid(Hanim.l_middle0);
        l_wrist.addChild(l_middle0);
        
        VJoint l_middle1 = new VJoint();
        l_middle1.setSid(Hanim.l_middle1);
        l_middle0.addChild(l_middle1);
        
        VJoint l_middle2 = new VJoint();
        l_middle2.setSid(Hanim.l_middle2);
        l_middle1.addChild(l_middle2);
        
        VJoint l_middle3 = new VJoint();
        l_middle3.setSid(Hanim.l_middle3);
        l_middle2.addChild(l_middle3);
        
        VJoint l_ring0 = new VJoint();
        l_ring0.setSid(Hanim.l_ring0);
        l_wrist.addChild(l_ring0);
        
        VJoint l_ring1 = new VJoint();
        l_ring1.setSid(Hanim.l_ring1);
        l_ring0.addChild(l_ring1);
        
        VJoint l_ring2 = new VJoint();
        l_ring2.setSid(Hanim.l_ring2);
        l_ring1.addChild(l_ring2);
        
        VJoint l_ring3 = new VJoint();
        l_ring3.setSid(Hanim.l_ring3);
        l_ring2.addChild(l_ring3);
        
        VJoint l_pinky0 = new VJoint();
        l_pinky0.setSid(Hanim.l_pinky0);
        l_wrist.addChild(l_pinky0);
        
        VJoint l_pinky1 = new VJoint();
        l_pinky1.setSid(Hanim.l_pinky1);
        l_pinky0.addChild(l_pinky1);
        
        VJoint l_pinky2 = new VJoint();
        l_pinky2.setSid(Hanim.l_pinky2);
        l_pinky1.addChild(l_pinky2);
        
        VJoint l_pinky3 = new VJoint();
        l_pinky3.setSid(Hanim.l_pinky3);
        l_pinky2.addChild(l_pinky3);
        
        VJoint r_sternoclavicular = new VJoint();
        r_sternoclavicular.setSid(Hanim.r_sternoclavicular);
        vt1.addChild(r_sternoclavicular);
        
        VJoint r_acromioclavicular = new VJoint();
        r_acromioclavicular.setSid(Hanim.r_acromioclavicular);
        r_sternoclavicular.addChild(r_acromioclavicular);
        
        VJoint r_shoulder = new VJoint();
        r_shoulder.setSid(Hanim.r_shoulder);
        r_acromioclavicular.addChild(r_shoulder);
        
        VJoint r_elbow = new VJoint();
        r_elbow.setSid(Hanim.r_elbow);
        r_shoulder.addChild(r_elbow);
        
        VJoint r_wrist = new VJoint();
        r_wrist.setSid(Hanim.r_wrist);
        r_elbow.addChild(r_wrist);
        
        VJoint r_thumb1 = new VJoint();
        r_thumb1.setSid(Hanim.r_thumb1);
        r_wrist.addChild(r_thumb1);
        
        VJoint r_thumb2 = new VJoint();
        r_thumb2.setSid(Hanim.r_thumb2);
        r_thumb1.addChild(r_thumb2);
        
        VJoint r_thumb3 = new VJoint();
        r_thumb3.setSid(Hanim.r_thumb3);
        r_thumb2.addChild(r_thumb3);
        
        VJoint r_index0 = new VJoint();
        r_index0.setSid(Hanim.r_index0);
        r_wrist.addChild(r_index0);
        
        VJoint r_index1 = new VJoint();
        r_index1.setSid(Hanim.r_index1);
        r_index0.addChild(r_index1);
        
        VJoint r_index2 = new VJoint();
        r_index2.setSid(Hanim.r_index2);
        r_index1.addChild(r_index2);
        
        VJoint r_index3 = new VJoint();
        r_index3.setSid(Hanim.r_index3);
        r_index2.addChild(r_index3);
        
        VJoint r_middle0 = new VJoint();
        r_middle0.setSid(Hanim.r_middle0);
        r_wrist.addChild(r_middle0);
        
        VJoint r_middle1 = new VJoint();
        r_middle1.setSid(Hanim.r_middle1);
        r_middle0.addChild(r_middle1);
        
        VJoint r_middle2 = new VJoint();
        r_middle2.setSid(Hanim.r_middle2);
        r_middle1.addChild(r_middle2);
        
        VJoint r_middle3 = new VJoint();
        r_middle3.setSid(Hanim.r_middle3);
        r_middle2.addChild(r_middle3);
        
        VJoint r_ring0 = new VJoint();
        r_ring0.setSid(Hanim.r_ring0);
        r_wrist.addChild(r_ring0);
        
        VJoint r_ring1 = new VJoint();
        r_ring1.setSid(Hanim.r_ring1);
        r_ring0.addChild(r_ring1);
        
        VJoint r_ring2 = new VJoint();
        r_ring2.setSid(Hanim.r_ring2);
        r_ring1.addChild(r_ring2);
        
        VJoint r_ring3 = new VJoint();
        r_ring3.setSid(Hanim.r_ring3);
        r_ring2.addChild(r_ring3);
        
        VJoint r_pinky0 = new VJoint();
        r_pinky0.setSid(Hanim.r_pinky0);
        r_wrist.addChild(r_pinky0);
        
        VJoint r_pinky1 = new VJoint();
        r_pinky1.setSid(Hanim.r_pinky1);
        r_pinky0.addChild(r_pinky1);
        
        VJoint r_pinky2 = new VJoint();
        r_pinky2.setSid(Hanim.r_pinky2);
        r_pinky1.addChild(r_pinky2);
        
        VJoint r_pinky3 = new VJoint();
        r_pinky3.setSid(Hanim.r_pinky3);
        r_pinky2.addChild(r_pinky3);
        
        return HumanoidRoot;
    }
    
    public static VJoint getLOA1HanimBody()
    {
        VJoint HumanoidRoot = new VJoint();
        HumanoidRoot.setSid(Hanim.HumanoidRoot);
        
        VJoint sacroiliac = new VJoint();
        sacroiliac.setSid(Hanim.sacroiliac);
        HumanoidRoot.addChild(sacroiliac);
        
        VJoint l_hip = new VJoint();
        l_hip.setSid(Hanim.l_hip);
        sacroiliac.addChild(l_hip);
        
        VJoint l_knee = new VJoint();
        l_knee.setSid(Hanim.l_knee);
        l_hip.addChild(l_knee);
        
        VJoint l_ankle = new VJoint();
        l_ankle.setSid(Hanim.l_ankle);
        l_knee.addChild(l_ankle);
        
        VJoint l_midtarsal = new VJoint();
        l_midtarsal.setSid(Hanim.l_midtarsal);
        l_ankle.addChild(l_midtarsal);
        
        VJoint r_hip = new VJoint();
        r_hip.setSid(Hanim.r_hip);
        sacroiliac.addChild(r_hip);
        
        VJoint r_knee = new VJoint();
        r_knee.setSid(Hanim.r_knee);
        r_hip.addChild(r_knee);
        
        VJoint r_ankle = new VJoint();
        r_ankle.setSid(Hanim.r_ankle);
        r_knee.addChild(r_ankle);
        
        VJoint r_midtarsal = new VJoint();
        r_midtarsal.setSid(Hanim.r_midtarsal);
        r_ankle.addChild(r_midtarsal);
        
        VJoint vl5 = new VJoint();
        vl5.setSid(Hanim.vl5);
        HumanoidRoot.addChild(vl5);
        
        VJoint skullbase = new VJoint();
        skullbase.setSid(Hanim.skullbase);
        vl5.addChild(skullbase);
        
        VJoint l_shoulder = new VJoint();
        l_shoulder.setSid(Hanim.l_shoulder);
        vl5.addChild(l_shoulder);
        
        VJoint l_elbow = new VJoint();
        l_elbow.setSid(Hanim.l_elbow);
        l_shoulder.addChild(l_elbow);
        
        VJoint l_wrist = new VJoint();
        l_wrist.setSid(Hanim.l_wrist);
        l_elbow.addChild(l_wrist);
        
        VJoint r_shoulder = new VJoint();
        r_shoulder.setSid(Hanim.r_shoulder);
        vl5.addChild(r_shoulder);
        
        VJoint r_elbow = new VJoint();
        r_elbow.setSid(Hanim.r_elbow);
        r_shoulder.addChild(r_elbow);
        
        VJoint r_wrist = new VJoint();
        r_wrist.setSid(Hanim.r_wrist);
        r_elbow.addChild(r_wrist);
        return HumanoidRoot;
    }
}
