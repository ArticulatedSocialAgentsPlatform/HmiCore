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
