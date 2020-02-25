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
 * A FACS AU.
 * 
 * @author PaulRC
 */
public class ActionUnit
{
    protected int index; // Sequence number.
    protected int number;

    public enum Symmetry
    {
        SYMMETRIC, ASYMMETRIC
    }

    protected Symmetry symmetry;
    protected String name;
    protected String muscle;

    /**
     * Constructor
     */
    public ActionUnit()
    {
    }

    /**
     * Constructor
     * 
     * @param index the index to set
     * @param number the number to set
     * @param symmetry the symmetry to set
     * @param name the name to set
     * @param muscle the muscle to set
     */
    public ActionUnit(int index, int number, Symmetry symmetry, String name, String musle)
    {
        this();

        this.index = index;
        this.number = number;
        this.symmetry = symmetry;
        this.name = name;
        this.muscle = musle;
    }

    /**
     * Constructor
     * 
     * @param index the index to set
     * @param number the number to set
     * @param symmetry the symmetry to set
     * @param name the name to set
     */
    public ActionUnit(int index, int number, Symmetry symmetry, String name)
    {
        this();

        this.index = index;
        this.number = number;
        this.symmetry = symmetry;
        this.name = name;
    }

    /**
     * @return the index
     */
    public int getIndex()
    {
        return index;
    }

    /**
     * @return the number
     */
    public int getNumber()
    {
        return number;
    }

    /**
     * @return the symmetry
     */
    public Symmetry getSymmetry()
    {
        return symmetry;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the muscle
     */
    public String getMuscle()
    {
        return muscle;
    }

    @Override
    public String toString()
    {
        StringBuffer retval = new StringBuffer();
        retval.append("[ActionUnit, index: " + index);
        retval.append(", number: " + number);
        retval.append(", symmetry: " + symmetry.toString());
        retval.append(", name: " + name);
        retval.append(", muscle: " + muscle);
        retval.append(']');
        return retval.toString();
    }
}