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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Map for Joint-Weigths, where L_[joint] = R_[joint] = [joint].
 * <p>
 * @author yannick-broeker
 */
class WeightMap implements Map<String, Float> {

    /**
     * Returns a default WeightMap, where each contined joint has weight 1.
     * <p>
     * The contained Joints are HUMANOID_ROOT, VC7, VL3, VT9, SKULLBASE, ACROMIOCLAVICULAR, ANKLE, ELBOW, HIP, KNEE,
     * SHOULDER and WRIST.
     * <p>
     * @return default WeightMap
     */
    public static WeightMap getDefaultInstance() {
        WeightMap weightMap = new WeightMap(16);

        weightMap.weights.put(Hanim.HumanoidRoot, 1f);
        weightMap.weights.put(Hanim.vc7, 1f);
        weightMap.weights.put(Hanim.vl3, 1f);
        weightMap.weights.put(Hanim.vt9, 1f);
        weightMap.weights.put(Hanim.skullbase, 0.1f);
        weightMap.weights.put(Hanim.l_acromioclavicular, 1f);
        weightMap.weights.put(Hanim.r_acromioclavicular, 1f);
        weightMap.weights.put(Hanim.l_ankle, 4f);
        weightMap.weights.put(Hanim.r_ankle, 4f);
        weightMap.weights.put(Hanim.r_elbow, 0.1f);
        weightMap.weights.put(Hanim.l_elbow, 0.1f);
        weightMap.weights.put(Hanim.r_hip, 10f);
        weightMap.weights.put(Hanim.l_hip, 10f);
        weightMap.weights.put(Hanim.r_knee, 10f);
        weightMap.weights.put(Hanim.l_knee, 10f);
        weightMap.weights.put(Hanim.r_shoulder, 10f);
        weightMap.weights.put(Hanim.l_shoulder, 10f);
        weightMap.weights.put(Hanim.l_wrist, 10f);
        weightMap.weights.put(Hanim.r_wrist, 10f);        

        weightMap.weights.put(Hanim.vt10, 1f);
        weightMap.weights.put(Hanim.vt6, 1f);
        weightMap.weights.put(Hanim.vt1, 1f);
        weightMap.weights.put(Hanim.r_sternoclavicular, 1f);
        weightMap.weights.put(Hanim.l_sternoclavicular, 1f);

        return weightMap;
    }

    /**
     * Map for storing weights.
     */
    private final Map<String, Float> weights;

    /**
     * Creates a new Weightmap.
     */
    public WeightMap() {
        weights = new HashMap<>();
    }

    /**
     * Creates a new Weightmap with initialCapacity {@code initialCapacity}.
     */
    public WeightMap(int initialCapacity) {
        weights = new HashMap<>(initialCapacity);
    }

    @Override
    public int size() {
        return weights.size();
    }

    @Override
    public boolean isEmpty() {
        return weights.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof String)) {
            return false;
        }
        if (((String) key).startsWith("L_") || ((String) key).startsWith("R_")) {
            return weights.containsKey(((String) key).substring(2));
        } else {
            return weights.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(Object value) {
        return weights.containsValue(value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Float> m) {
        for (Entry<? extends String, ? extends Float> entrySet : m.entrySet()) {
            String key = entrySet.getKey();
            Float value = entrySet.getValue();
            this.put(key, value);
        }
    }

    @Override
    public void clear() {
        weights.clear();
    }

    @Override
    public Set<String> keySet() {
        return weights.keySet();
    }

    @Override
    public Collection<Float> values() {
        return weights.values();
    }

    @Override
    public Set<Entry<String, Float>> entrySet() {
        return weights.entrySet();
    }

    @Override
    public Float get(Object key) {
        if (!(key instanceof String) && (((String) key).startsWith("L_") || ((String) key).startsWith("R_"))) {
            return weights.get(((String) key).substring(2));

        } else {
            return weights.get(key);
        }
    }

    @Override
    public Float put(String key, Float value) {
        if (key.startsWith("L_") || key.startsWith("R_")) {
            return weights.put(key.substring(2), value);
        } else {
            return weights.put(key, value);
        }
    }

    @Override
    public Float remove(Object key) {
        if (!(key instanceof String) && (((String) key).startsWith("L_") || ((String) key).startsWith("R_"))) {
            return weights.remove(((String) key).substring(2));

        } else {
            return weights.remove(key);
        }
    }

}
