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
