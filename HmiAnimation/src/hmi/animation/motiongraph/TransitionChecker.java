package hmi.animation.motiongraph;

import hmi.animation.SkeletonInterpolator;

/**
 * Checks if two skeletoninterpolator frames are close enough for a transition
 * @author herwinvw
 *
 */
public interface TransitionChecker
{
    boolean allowTransition(SkeletonInterpolator skiIn, SkeletonInterpolator skiOut, int frameIn, int FrameOut);
}
