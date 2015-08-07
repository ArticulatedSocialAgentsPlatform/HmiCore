package hmi.animation.motiongraph.alignment;

import hmi.animation.ConfigList;
import hmi.animation.SkeletonInterpolator;
import hmi.math.Quat4f;
import hmi.math.Vec3f;

/**
 * Created by Zukie on 26/06/15.
 * TODO: Align y-rotation
 *
 * @author Zukie
 */
public class Alignment implements IAlignment {


    /**
     * {@inheritDoc}
     */
    @Override
    public SkeletonInterpolator align(SkeletonInterpolator first, SkeletonInterpolator second, int frames) {

        float[] config;
        ConfigList configList = new ConfigList(second.getConfigSize());
        String configType = second.getConfigType();
        String[] partIds = second.getPartIds().clone();

        for (int i = 0; i < partIds.length; i++) {
            partIds[i] = second.getPartIds()[i];
        } // copy second.partIds

        float[] firstConfig = first.getConfig(first.size() - frames); // Frame where blending starts



        float[] quat1 = {firstConfig[Quat4f.S + 3], firstConfig[Quat4f.X + 3], firstConfig[Quat4f.Y + 3], firstConfig[Quat4f.Z + 3]};
        //Quaternion of first motion

        float[] firstRollPitchYaw = new float[3];
        Quat4f.getRollPitchYaw(quat1, firstRollPitchYaw);

        float[] secondConf = {second.getConfig(0)[Quat4f.S + 3], second.getConfig(0)[Quat4f.X + 3],
                second.getConfig(0)[Quat4f.Y + 3], second.getConfig(0)[Quat4f.Z + 3]};
        float[] secondRollPitchYawConf0 = new float[3];
        Quat4f.getRollPitchYaw(secondConf, secondRollPitchYawConf0);

        for (int i = 0; i < second.getConfigList().size(); i++) {
            config = second.getConfig(i).clone();

            // Adjust Translation
            config[Vec3f.X] = config[Vec3f.X] - second.getConfig(0)[Vec3f.X] + firstConfig[Vec3f.X];
            config[Vec3f.Y] = config[Vec3f.Y] - second.getConfig(0)[Vec3f.Y] + firstConfig[Vec3f.Y];
            config[Vec3f.Z] = config[Vec3f.Z] - second.getConfig(0)[Vec3f.Z] + firstConfig[Vec3f.Z];


            //Adjust Rotation

            float[] quat2 = {config[Quat4f.S + 3], config[Quat4f.X + 3], config[Quat4f.Y + 3], config[Quat4f.Z + 3]};
            // Quaterninon of second motion

            float[] secondRollPitchYaw = new float[3];
            Quat4f.getRollPitchYaw(quat2, secondRollPitchYaw);

            secondRollPitchYaw[2] = secondRollPitchYaw[2] - secondRollPitchYawConf0[2] + firstRollPitchYaw[2];

            Quat4f.setFromRollPitchYaw(quat2, secondRollPitchYaw[0], secondRollPitchYaw[1], secondRollPitchYaw[2]);
            config[Quat4f.S + 3] = quat2[Quat4f.S];
            config[Quat4f.X + 3] = quat2[Quat4f.X];
            config[Quat4f.Y + 3] = quat2[Quat4f.Y];
            config[Quat4f.Z + 3] = quat2[Quat4f.Z];

            configList.addConfig(second.getTime(i), config); //Set new config for new SkeletonInterplator

        }
        SkeletonInterpolator newSecond = new SkeletonInterpolator();
        newSecond.setConfigList(configList);
        newSecond.setConfigType(configType);
        newSecond.setPartIds(partIds);
        /*
         (Translation:[0]-[2];Rotation:[3]-[6])
         TODO Rotation anpassen.
         */
        return newSecond;
    }
}
