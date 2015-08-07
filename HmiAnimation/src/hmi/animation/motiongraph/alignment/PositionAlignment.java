package hmi.animation.motiongraph.alignment;

import hmi.animation.ConfigList;
import hmi.animation.SkeletonInterpolator;
import hmi.math.Vec3f;

/**
 * Aligns the root position of two interpolators
 * @author hvanwelbergen
 *
 */
public class PositionAlignment implements IAlignment 
{
    @Override
    public SkeletonInterpolator align(SkeletonInterpolator first, SkeletonInterpolator second, int frames)
    {
        float[] config;
        ConfigList configList = new ConfigList(second.getConfigSize());
        String configType = second.getConfigType();
        String[] partIds = second.getPartIds().clone();

        for (int i = 0; i < partIds.length; i++) {
            partIds[i] = second.getPartIds()[i];
        } // copy second.partIds

        float[] firstConfig = first.getConfig(first.size() - frames); // Frame where blending starts
        
        for (int i = 0; i < second.getConfigList().size(); i++) {
            config = second.getConfig(i).clone();

            // Adjust Translation
            config[Vec3f.X] = config[Vec3f.X] - second.getConfig(0)[Vec3f.X] + firstConfig[Vec3f.X];
            config[Vec3f.Y] = config[Vec3f.Y] - second.getConfig(0)[Vec3f.Y] + firstConfig[Vec3f.Y];
            config[Vec3f.Z] = config[Vec3f.Z] - second.getConfig(0)[Vec3f.Z] + firstConfig[Vec3f.Z];
            configList.addConfig(second.getTime(i), config); //Set new config for new SkeletonInterplator

        }
        SkeletonInterpolator newSecond = new SkeletonInterpolator();
        newSecond.setConfigList(configList);
        newSecond.setConfigType(configType);
        newSecond.setPartIds(partIds);        
        return newSecond;
    }
    
}
