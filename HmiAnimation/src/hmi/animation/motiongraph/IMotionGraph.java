package hmi.animation.motiongraph;

import hmi.animation.SkeletonInterpolator;

/**
 * Interface for Motiongraphs.
 * <p> 
* @author yannick-broeker
 */
public interface IMotionGraph {

    /**
     * Returns next motion to be displayed.
     * <p>
     * @return Skeletoninterpolator next.
     */
    public abstract SkeletonInterpolator next();

}
