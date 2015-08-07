package hmi.animation.motiongraph.blending;

import hmi.animation.SkeletonInterpolator;

/**
 * Interface for Blendings.
 * <p>
 * @author yannick-broeker
 */
public interface IBlend {

    /**
     * Returns the Blending of {@code frames} between the two Motions.
     * <p>
     * @param first first Motion
     * @param second second Motion
     * @param frames number of Frames to use
     * @return Blending of the two Motions
     */
    SkeletonInterpolator blend(SkeletonInterpolator first, SkeletonInterpolator second, int frames);
}
