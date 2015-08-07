package hmi.animation.motiongraph.metrics;

import hmi.animation.Hanim;
import hmi.animation.SkeletonInterpolator;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author yannick-broeker
 */
public class Equals implements IEquals {

    /**
     * Compares last frame of first and first frame of second.
     * <p>
     * @param start first Motion
     * @param end second motion
     * @return true, if last frame of first an first frame of second equal.
     */
    @Override
    public boolean startEndEquals(SkeletonInterpolator start, SkeletonInterpolator end) {
        float[] endConfig = end.getConfig(0);
        Map<String, float[]> endConfigMap = new HashMap<String, float[]>();

        float[] startConfig = start.getConfig(start.getConfigList().size() - 1);
        Map<String, float[]> startConfigMap = new HashMap<String, float[]>();

        if (!start.getConfigType().equals(end.getConfigType())) {
            return false;//erstmal nur genau gleiche, besser transform ignorieren
        }

        if (startConfig.length != endConfig.length) {
            return false;//erstmal nur genau gleiche, besser transform ignorieren
        }

        int i = 0;
        int part = 0;
        if (start.getConfigType().contains(SkeletonInterpolator.ROOT_TRANSFORM)) {
            //startConfigMap.put("Util.ROOT_TRANSFORM", new float[]{startConfig[X], startConfig[Y], startConfig[Z]});
            //endConfigMap.put("Util.ROOT_TRANSFORM", new float[]{endConfig[X], endConfig[Y], endConfig[Z]});
            i += 3;
        }

        for (part = 0; part < end.getPartIds().length; part++) {
            endConfigMap.put(end.getPartIds()[part], new float[]{
                endConfig[i + part * 3], 
                endConfig[i + 1 + part * 3], 
                endConfig[i + 2 + part * 3], 
                endConfig[i + 3 + part * 3]});
            startConfigMap.put(start.getPartIds()[part], new float[]{
                startConfig[i + part * 3], 
                startConfig[i + 1 + part * 3], 
                startConfig[i + 2 + part * 3], 
                startConfig[i + 3 + part * 3]});
        }

        for (Map.Entry<String, float[]> entrySet : startConfigMap.entrySet()) {
            String startKey = entrySet.getKey();
            if (startKey.equals(Hanim.HumanoidRoot)) {
                continue;
            }
            float[] startValue = entrySet.getValue();

            float[] endValue = endConfigMap.get(startKey);
            for (int j = 0; j < startValue.length; j++) {
                if (startValue[j] != endValue[j]) {
                    return false;
                }
            }
        }

        return true;

    }

}
