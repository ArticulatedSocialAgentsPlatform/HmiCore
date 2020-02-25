/*******************************************************************************
 * Copyright (C) 2009-2020 Human Media Interaction, University of Twente, the Netherlands
 *
 * This file is part of the Articulated Social Agents Platform BML realizer (ASAPRealizer).
 *
 * ASAPRealizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ASAPRealizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ASAPRealizer.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
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
