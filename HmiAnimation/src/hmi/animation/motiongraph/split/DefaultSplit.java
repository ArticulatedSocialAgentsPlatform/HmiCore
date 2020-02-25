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
package hmi.animation.motiongraph.split;

import hmi.animation.SkeletonInterpolator;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author yannick-broeker
 */
public class DefaultSplit implements ISplit {

    public static final double MIN_SPLIT_TIME = 2.5;

    @Override
    public List<SkeletonInterpolator> split(SkeletonInterpolator skeletonInterpolator) {
        List<SkeletonInterpolator> motions = new LinkedList<>();

        double length = skeletonInterpolator.getEndTime() - skeletonInterpolator.getStartTime(); //Get boundary for splitting

        if ( MIN_SPLIT_TIME < length) {

            int splits = (int) (length / (MIN_SPLIT_TIME));
            int splitlength = skeletonInterpolator.size() / splits;

            for (int i = 0; i < splits - 1; i++) {
                motions.add(skeletonInterpolator.subSkeletonInterpolator(i * splitlength, (i + 1) * splitlength));
            }
            motions.add(skeletonInterpolator.subSkeletonInterpolator((splits - 1) * splitlength));
        } else {
            motions.add(skeletonInterpolator);
        }

        return motions;
    }

}
