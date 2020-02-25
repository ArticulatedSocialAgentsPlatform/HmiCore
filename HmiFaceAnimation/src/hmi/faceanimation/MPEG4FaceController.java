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
package hmi.faceanimation;

import hmi.faceanimation.model.MPEG4Configuration;

/**
 * Controls the face using MPEG4
 * @author hvanwelbergen
 *
 */
public interface MPEG4FaceController
{
    /**
     * Directly set a FAP configuration on the face. FAPs that are assigned NULL
     * values will not be modified at all. All other FAPs will be completely
     * overwritten with the new configuration, i.e., no blending is done.
     */
    void setMPEG4Configuration(MPEG4Configuration config);

    void addMPEG4Configuration(MPEG4Configuration config);

    void removeMPEG4Configuration(MPEG4Configuration config);
    
    /**
     * Do actually apply the current MPEG4 configurations to the face
     */
    void copy();
}
