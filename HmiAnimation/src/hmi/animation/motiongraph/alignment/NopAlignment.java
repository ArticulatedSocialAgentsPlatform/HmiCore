package hmi.animation.motiongraph.alignment;

import hmi.animation.SkeletonInterpolator;

/**
 * Created by Zukie on 03/07/15.
 */
public class NopAlignment implements IAlignment {

    @Override
    public SkeletonInterpolator align(SkeletonInterpolator first, SkeletonInterpolator second, int frames) {
        return second;
    }
}
