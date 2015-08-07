package hmi.animation.motiongraph.metrics;

import hmi.animation.SkeletonInterpolator;

/**
 * Interface for Distance-Metric.
 * <p>
 * @author yannick-broeker
 */
public interface IDistance {

    /**
     * Computes the distance between {@code start} and {@code end}.
     * <p>
     * The number of frames used for comparing depends on implementation.
     * <p>
     * @param start First Motion
     * @param end Second Motion
     * @return calculated distance
     * @deprecated Used for testing purposes, better use
     * {@link #distance(SkeletonInterpolator start, SkeletonInterpolator end, int frames)}.
     */
    @Deprecated
    double distance(SkeletonInterpolator start, SkeletonInterpolator end);

    /**
     * Computes the distance between {@code start} and {@code end} at the {@code startFrame}-last Frame of {@code start}
     * and Frame {@code endFrame} of {@code end}. The calculated distance is the sum of the distance of each compared
     * frames. {@code startFrame=1} means the last frame of {@code start}. {@code endFrame=0} means the first frame of
     * {@code end}.
     * <p>
     * @param start First Motion
     * @param end Second Motion
     * @param startFrame frame of {@code start}
     * @param endFrame frame of {@code end}
     * @return calculated distance
     */
    double distance(SkeletonInterpolator start, SkeletonInterpolator end, int startFrame, int endFrame);

    /**
     * Computes the distance between the last {@code frames} Frames of {@code start} and the first {@code frames} Frames
     * of {@code end}.
     * <p>
     * @param start First Motion
     * @param end Second Motion
     * @param frames Number of Frames to use to compare the Motions
     * @return calculated distance
     */
    double distance(SkeletonInterpolator start, SkeletonInterpolator end, int frames);

}
