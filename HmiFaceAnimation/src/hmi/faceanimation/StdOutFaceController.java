/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
package hmi.faceanimation;

import hmi.faceanimation.model.MPEG4Configuration;

import java.util.ArrayList;

/**
 * Dummy implementation that dumps all calls to stdOut
 */
public class StdOutFaceController implements FaceController
{
    ArrayList<String> possibleMorphTargetNames = new ArrayList<String>();

    public void setMPEG4Configuration(MPEG4Configuration config)
    {
        System.out.println("Set MPEG4 configuration: " + config.toString());
    }

    public void addMPEG4Configuration(MPEG4Configuration config)
    {
        System.out.println("Add MPEG4 configuration: " + config.toString());
    }

    public void removeMPEG4Configuration(MPEG4Configuration config)
    {
        System.out.println("Remove MPEG4 configuration: " + config.toString());
    }

    public void addMorphTargets(String[] targetNames, float[] weights)
    {
        System.out.println("Add morph targets: ");
        for (int i = 0; i < targetNames.length; i++)
        {
            System.out.println(targetNames[i] + ": " + weights[i]);
        }
    }

    public void removeMorphTargets(String[] targetNames, float[] weights)
    {
        System.out.println("Remove morph targets: ");
        for (int i = 0; i < targetNames.length; i++)
        {
            System.out.println(targetNames[i] + ": " + weights[i]);
        }
    }

    public ArrayList<String> getPossibleFaceMorphTargetNames()
    {
        return possibleMorphTargetNames;
    }

    public void setPossibleFaceMorphTargetNames(ArrayList<String> names)
    {
        possibleMorphTargetNames = new ArrayList<String>(names);
    }

    public void copy()
    {
        System.out.println("copy face");
    }

    @Override
    public void setMorphTargets(String[] targetNames, float[] weights)
    {
        System.out.println("Set morph targets: ");
        for (int i = 0; i < targetNames.length; i++)
        {
            System.out.println(targetNames[i] + ": " + weights[i]);
        }
    }

    @Override
    public float getCurrentWeight(String targetName)
    {
        return 0;
    }

}
