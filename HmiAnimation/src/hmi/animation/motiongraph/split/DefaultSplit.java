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
