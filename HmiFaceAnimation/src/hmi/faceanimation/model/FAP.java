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

/**
 * An MPEG-4 FA FAP.
 * 
 * Notice: tongue_roll has two feature points, but this is yet to be accounted for. We ignore it for now since our facial animation engine will not be
 * able to animate this part.
 * 
 * @author PaulRC
 */
public class FAP
{
    public int index;
    public int number;
    private String name;
    private String description;

    public enum Unit
    {
        NA, MNS, MW, IRISD, AU, ES, ENS
    };

    private Unit unit;

    public enum Directionality
    {
        NA, UNIDIRECTIONAL, BIDIRECTIONAL
    };

    private Directionality directionality;

    public enum Direction
    {
        NA, DOWN, UP, LEFT, RIGHT, FORWARD, GROWING, CONCAVE_UPWARD
    };

    private Direction direction;
    private FeaturePoint fp;
    private FAP otherSide;

    /**
     * Constructor
     */
    public FAP()
    {
        unit = Unit.NA;
        directionality = Directionality.NA;
        direction = Direction.NA;
    }

    /**
     * Constructor
     */
    public FAP(int index, int number, String name, String description, Unit unit, Directionality directionality, Direction direction, FeaturePoint fp)
    {
        this();

        this.index = index;
        this.number = number;
        this.name = name;
        this.description = description;
        this.unit = unit;
        this.directionality = directionality;
        this.direction = direction;
        this.fp = fp;
    }

    /**
     * Constructor for multi-purpose FAPs (viseme, expression).
     */
    public FAP(int index, int number, String name, String description, FeaturePoint fp)
    {
        this();

        this.index = index;
        this.number = number;
        this.name = name;
        this.description = description;
        this.fp = fp;
    }

    /**
     * @return the index
     */
    public int getIndex()
    {
        return index;
    }

    public int getNumber()
    {
        return number;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @return the unit
     */
    public Unit getUnit()
    {
        return unit;
    }

    /**
     * @return the directionality
     */
    public Directionality getDirectionality()
    {
        return directionality;
    }

    /**
     * @return the direction
     */
    public Direction getDirection()
    {
        return direction;
    }

    /**
     * @return the feature point
     */
    public FeaturePoint getFeaturePoint()
    {
        return fp;
    }

    /**
     * @return the other side
     */
    public FAP getOtherSide()
    {
        return otherSide;
    }

    /**
     * Sets the other side of this FAP (e.g., stretch_l_cornerlip for stretch_r_cornerlip)
     * 
     * @param otherSide
     */
    public void setOtherSide(FAP otherSide)
    {
        this.otherSide = otherSide;
    }

    @Override
    public String toString()
    {
        return number + ": " + name;
    }

    public int hashCode()
    {
        return name.hashCode();
    }

    public boolean equals(Object obj)
    {
        if (!(obj instanceof FAP))
            return false;
        return equals((FAP) obj);
    }

    public boolean equals(FAP fap)
    {
        return name.equals(fap.getName());
    }
}
