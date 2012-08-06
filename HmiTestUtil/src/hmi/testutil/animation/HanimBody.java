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
