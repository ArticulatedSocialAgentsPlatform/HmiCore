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
