package hmi.animation.motiongraph.blending;

import hmi.animation.ConfigList;
import hmi.animation.SkeletonInterpolator;
import hmi.animation.motiongraph.alignment.IAlignment;
import hmi.math.Quat4f;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Zukie on 24/06/15.
 * @author Zukie
 */
public class Blend implements IBlend {

    IAlignment align;

    public Blend(IAlignment align) {
        this.align = align;
    }

    @Override
    public SkeletonInterpolator blend(SkeletonInterpolator first, SkeletonInterpolator second, int frames) {
        SkeletonInterpolator blendedMotion = new SkeletonInterpolator();
        String[] partIds = null;

        second = align.align(first, second, frames);

        ConfigList configList = new ConfigList(second.getConfigSize());
        blendedMotion.setConfigType(second.getConfigType());

        blendedMotion.setConfigList(configList);

        for (int frame = 0; frame < frames; frame++) {

            Map<String, float[]> configMap1 = new HashMap<>(first.getPartIds().length);
            Map<String, float[]> configMap2 = new HashMap<>(second.getPartIds().length);
            List<String> keys = new LinkedList<>();
            //create the new config
            float[] newConf = second.getConfig(frame).clone();


            // Adjust Transizion of fist motion
            float x1 = first.getConfig((first.size() - frames) + frame)[0];
            float y1 = first.getConfig((first.size() - frames) + frame)[1];
            float z1 = first.getConfig((first.size() - frames) + frame)[2];


            //adjust transition of second motion
            float x2 = second.getConfig(frame)[0];
            float y2 = second.getConfig(frame)[1];
            float z2 = second.getConfig(frame)[2];


            newConf[0] = (float) blendWeights(frame, frames) * x1 + (float) (1 - blendWeights(frame, frames)) * x2;
            newConf[1] = (float) blendWeights(frame, frames) * y1 + (float) (1 - blendWeights(frame, frames)) * y2;
            newConf[2] = (float) blendWeights(frame, frames) * z1 + (float) (1 - blendWeights(frame, frames)) * z2;

            //Adjust Rotation
            int index1 = 3; //rotation values start at 3rd position in array
            int index2 = 3;

            for (int part = 0; part < first.getPartIds().length; part++) {
                configMap1.put(first.getPartIds()[part], new float[]{
                        first.getConfig(frame)[index1 + part * 4],
                        first.getConfig(frame)[index1 + part * 4 + 1],
                        first.getConfig(frame)[index1 + part * 4 + 2],
                        first.getConfig(frame)[index1 + part * 4 + 3]});
                if (!keys.contains(first.getPartIds()[part])) {
                    keys.add(first.getPartIds()[part]);
                }
            }
            for (int part = 0; part < second.getPartIds().length; part++) {
                configMap2.put(second.getPartIds()[part], new float[]{
                        second.getConfig(frame)[index2 + part * 4],
                        second.getConfig(frame)[index2 + part * 4 + 1],
                        second.getConfig(frame)[index2 + part * 4 + 2],
                        second.getConfig(frame)[index2 + part * 4 + 3]});
                if (!keys.contains(second.getPartIds()[part])) {
                    keys.add(second.getPartIds()[part]);
                }
            }



            if (partIds == null) {
                partIds = new String[keys.size()];
                for (int i = 0; i < keys.size(); i++) {
                    //set new partIds
                    partIds[i] = keys.get(i);
                }
                blendedMotion.setPartIds(partIds);
            }


            for (int i = 0; i < keys.size(); i++) {
                float[] rotation1 = configMap1.get(keys.get(i));
                float[] rotation2 = configMap2.get(keys.get(i));


                //blend the two frames
                //Quat4f.interpolate.alpha = 1-alpha;
                Quat4f.interpolate(rotation1, rotation2, 1f - (float) blendWeights(frame, frames));


                //adjust new configs
                newConf[3 + i * 4] = rotation1[0];
                newConf[3 + i * 4 + 1] = rotation1[1];
                newConf[3 + i * 4 + 2] = rotation1[2];
                newConf[3 + i * 4 + 3] = rotation1[3];

            }


            blendedMotion.getConfigList().addConfig(second.getTime(frame), newConf);
        }

        return blendedMotion;
    }

    /**
     * Calculate blendweights.
     *
     * @param frame          frame to be weighted
     * @param numberOfFrames
     * @return blendweigths for frame
     */
    private double blendWeights(int frame, int numberOfFrames) {

        double blendWeights = 2f * Math.pow((float)(frame + 1) / (float)numberOfFrames, 3) - 3f * Math.pow((float)(frame + 1) / (float)numberOfFrames, 2) + 1f;

        return blendWeights;

    }


}
