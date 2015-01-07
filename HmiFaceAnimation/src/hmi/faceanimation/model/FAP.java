/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2015 University of Twente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
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
