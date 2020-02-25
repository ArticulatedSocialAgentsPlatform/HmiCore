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
package hmi.faceanimation.model;

import com.google.common.base.Objects;

/**
 * This class represents a MPEG-4 feature point. String representation is in the form <code>x.x</code> and is build up from a group number and a point
 * number. The following groups exist:
 * <ol>
 * <li>High level expressions and visemes (not used)</li>
 * <li>Inner lips</li>
 * <li>Eyes</li>
 * <li>Eyebrows</li>
 * <li>Cheeks</li>
 * <li>Tongue</li>
 * <li>Head</li>
 * <li>Outer lips</li>
 * <li>Nose</li>
 * <li>Ears</li>
 * </ol>
 * 
 * @author ronald
 */
public class FeaturePoint
{
    private int groupNr;
    private int pointNr;

    public FeaturePoint(int groupNr, int pointNr)
    {
        this.groupNr = groupNr;
        this.pointNr = pointNr;
    }

    public FeaturePoint(String fp)
    {
        String[] elements = fp.split("\\.");
        if (elements.length == 2)
        {
            groupNr = Integer.parseInt(elements[0]);

            if (elements[1].contains(","))
                elements[1] = elements[1].substring(0, 1);
            pointNr = Integer.parseInt(elements[1]);
        }
    }

    public String toString()
    {
        return groupNr + "." + pointNr;
    }

    public int getGroupNr()
    {
        return groupNr;
    }

    public void setGroupNr(int groupNr)
    {
        this.groupNr = groupNr;
    }

    public int getPointNr()
    {
        return pointNr;
    }

    public void setPointNr(int pointNr)
    {
        this.pointNr = pointNr;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(pointNr, groupNr);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof FeaturePoint))
            return false;
        return equals((FeaturePoint) obj);
    }

    public boolean equals(FeaturePoint fp)
    {
        return toString().equals(fp.toString());
    }
}
