package hmi.animation.motiongraph.alignment;

import hmi.animation.SkeletonInterpolator;

/**
 * Created by Zukie on 26/06/15.
 * <p>
 * @author Zukie
 */
public interface IAlignment {

    /**
     * Align motion's root positions before blending.
     * <p>
     * @param first First motion
     * @param second motion to be blended in.
     * @param frames number of Frames to be aligned.
     * @return Aligned SkeletonInterpolator. It's not guaranteed to be an new instance, also {@code first} or
     * {@code second} can be modified.
     */
    SkeletonInterpolator align(SkeletonInterpolator first, SkeletonInterpolator second, int frames);
}
