package hmi.animation.motiongraph;

import lombok.Getter;
import hmi.animation.SkeletonInterpolator;
import hmi.animation.VJoint;

/**
 * Motiongraph edge, containing an animation
 * @author herwinvw
 */
public class MGEdge
{
    @Getter
    private final SkeletonInterpolator motion;
    
    @Getter
    private final MGNode outgoingNode;
    
    @Getter
    private final MGNode incomingNode;
    
    public MGEdge(SkeletonInterpolator motion, MGNode incomingNode, MGNode outgoingNode)
    {
        this.motion = motion;
        this.outgoingNode = outgoingNode;
        this.incomingNode = incomingNode;
        incomingNode.addEdge(this);
    }
    
    public double getDuration()
    {
        return motion.getEndTime()-motion.getStartTime();
    }
    
    public void play(double t)
    {
        motion.time(motion.getStartTime()+t);
    }
    
    public void setTarget(VJoint human)
    {
        motion.setTarget(human);
    }
}
