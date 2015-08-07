package hmi.animation.motiongraph.metrics;

import hmi.animation.SkeletonInterpolator;
import hmi.animation.motiongraph.alignment.IAlignment;
import hmi.math.Quat4f;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of {@code IDistance} which compares Joint Angles.
 * <p>
 * TODO: Its based on frames, so both SkeletonInterpolators needs to habe the same, constant frameRate. Better frame at
 * time t to compare both as frame i.
 * <p>
 * TODO: Align compared frames, yet SkeletonInterpolator isn't like the same but translated or rotated
 * SkeletonInterpolator.
 * <p>
 * @author yannick-broeker
 */
public final class JointAngles implements IDistance {

    /**
     * Default number of frames to be compared.
     */
    public static final int DEFAULT_COMPARED_FRAMES = 10;
    /**
     * Weights for Joints
     */
    private final Map<String, Float> weights = WeightMap.getDefaultInstance();
    /**
     * Aligment used to align motions. 
     */
    private final IAlignment align;

    /**
     * Creates a new DistanceMetric based on comparing JointAngles.
     * 
     * @param align Aligment used to align motions.
     */
    public JointAngles(IAlignment align) {
        this.align = align;
    }

    /**
     * {@inheritDoc} This implementation calls
     * {@link #distance(SkeletonInterpolator, SkeletonInterpolator, int)} with
     * {@code frames =}{@link #DEFAULT_COMPARED_FRAMES}.
     */
    @Override
    public double distance(SkeletonInterpolator start, SkeletonInterpolator end) {
        return distance(start, end, DEFAULT_COMPARED_FRAMES);
    }

    /**
     * {@inheritDoc} This implementation calls
     * {@link #distance(SkeletonInterpolator, SkeletonInterpolator, int, int)} for each
     * pair of frames und sums the distances up.
     */
    @Override
    public double distance(SkeletonInterpolator start, SkeletonInterpolator end, int frames) {

        SkeletonInterpolator endAligned = align.align(start, end, frames);

        
        float totalDist = 0;
        for (int i = 0; i < frames; i++) {
            totalDist += distance(start, endAligned, frames -i, i);
        }

        return totalDist;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double distance(SkeletonInterpolator start, SkeletonInterpolator end, int startFrame, int endFrame) {
try {

    //start.size() - startFrame = current Frame, so +1 for number of Frames
        
        //TODO
        return dist(start.getConfig(start.size() - startFrame), end.getConfig(endFrame),
                start.getConfigType(), end.getConfigType(),
                start.getPartIds(), end.getPartIds());
        } catch(Exception e) {System.err.println(start.size() - startFrame);throw e;}

    }

    private double dist(float[] config1, float[] config2, String configType1, String configType2, String[] partIds1, String[] partIds2) {
        int index1 = 0, index2 = 0;
        double rootTransformDist = 0;
        double rotDist = 0;
        Map<String, float[]> configMap1 = new HashMap<String, float[]>();
        Map<String, float[]> configMap2 = new HashMap<String, float[]>();
        Set<String> keys = new HashSet<>();

        //Root-Translation
        if (configType1.contains(SkeletonInterpolator.ROOT_TRANSFORM) && configType2.contains(SkeletonInterpolator.ROOT_TRANSFORM)) {
            for (; index1 < 3; index1++, index2++) {
                rootTransformDist += Math.pow(config1[index1] - config2[index2], 2);
            }
        } else if (configType1.contains(SkeletonInterpolator.ROOT_TRANSFORM)) {
            index1 += 3;
        } else if (configType2.contains(SkeletonInterpolator.ROOT_TRANSFORM)) {
            index2 += 3;
        }

        //Translation
        if ((!configType1.contains(SkeletonInterpolator.ROOT_TRANSFORM) && configType1.contains(SkeletonInterpolator.TRANSLATION))
                && (!configType2.contains(SkeletonInterpolator.TRANSLATION) && configType2.contains(SkeletonInterpolator.TRANSLATION))) {
            //MAYBE TODO
            index1 += partIds1.length * 3;
            index1 += partIds2.length * 3;
        } else if ((!configType1.contains(SkeletonInterpolator.ROOT_TRANSFORM) && configType1.contains(SkeletonInterpolator.TRANSLATION))) {
            index1 += partIds1.length * 3;
        } else if ((!configType2.contains(SkeletonInterpolator.TRANSLATION) && configType2.contains(SkeletonInterpolator.TRANSLATION))) {
            index1 += partIds2.length * 3;
        }

        //Rotation
        if (configType1.contains(SkeletonInterpolator.ROTATION) && configType2.contains(SkeletonInterpolator.ROTATION)) {
            for (int part = 0; part < partIds1.length; part++) {
                configMap1.put(partIds1[part], new float[]{
                    config1[index1 + part * 4],
                    config1[index1 + part * 4 + 1],
                    config1[index1 + part * 4 + 2],
                    config1[index1 + part * 4 + 3]});
                keys.add(partIds1[part]);
            }
            for (int part = 0; part < partIds2.length; part++) {
                configMap2.put(partIds2[part], new float[]{
                    config2[index2 + part * 4],
                    config2[index2 + part * 4 + 1],
                    config2[index2 + part * 4 + 2],
                    config2[index2 + part * 4 + 3]});
                keys.add(partIds2[part]);
            }
            index1 += partIds1.length * 4;
            index2 += partIds2.length * 4;

            for (String key : keys) {
                float[] rotation1 = configMap1.get(key);
                float[] rotation2 = configMap2.get(key);
                Float weight = weights.get(key);
                weight = weight != null ? weight : 1;
                if (rotation1 != null && rotation2 != null) {
                    rotDist += weight * quaternionNorm(rotation1, rotation2);
                }

            }

        } else if (configType1.contains(SkeletonInterpolator.ROTATION)) {
            index1 += partIds1.length * 4;
        } else if (configType2.contains(SkeletonInterpolator.ROTATION)) {
            index2 += partIds2.length * 4;
        }

        return rootTransformDist + rotDist;
    }

    private double quaternionNorm(float[] quat1, float[] quat2) {
        float[] v1 = new float[3];
        float[] v2 = new float[3];
        Quat4f.log(v1, quat1);
        Quat4f.log(v2, quat2);

        //float dot = Vec3f.dot(v1, v2);
        //Math.log(dot);
        return (v1[0] - v2[0]) * (v1[0] - v2[0])
                + (v1[1] - v2[1]) * (v1[1] - v2[1])
                + (v1[2] - v2[2]) * (v1[2] - v2[2]);

    }

}
