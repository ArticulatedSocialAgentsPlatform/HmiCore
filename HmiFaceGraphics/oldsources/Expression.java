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
package hmi.animation;

public class Expression
{
    protected String name;
    protected MuscleSet muscleSet;
    protected float muscleSettings[];

    public Expression(String name, MuscleSet muscleSet)
    {
        this.name = name;
        this.muscleSet = muscleSet;
        muscleSettings = new float[muscleSet.getMuscleSettings().length];
        System.arraycopy(muscleSet.getMuscleSettings(), 0, muscleSettings, 0, muscleSettings.length);
    }

    public void setDefault()
    {
        // first check if the muscleset didn't change, if it did then reset the muscle settings
        // TODO: make a method for copy existing values, otherwise the complete expression must be remade;
        if (muscleSettings.length != muscleSet.getMuscleSettingsCount())
        {
            muscleSettings = new float[muscleSet.getMuscleSettings().length];
            System.arraycopy(muscleSet.getMuscleSettings(), 0, muscleSettings, 0, muscleSettings.length);
        }
        muscleSet.setMuscleSettings(muscleSettings);
        muscleSet.setDefault();
    }

    public MuscleSet getMuscleSet()
    {
        return muscleSet;
    }

    public void setMuscleSet(MuscleSet muscleSet)
    {
        this.muscleSet = muscleSet;
    }

    public float[] getMuscleSettings()
    {
        return muscleSettings;
    }

    public void setMuscleSettings(float[] muscleSettings)
    {
        this.muscleSettings = muscleSettings;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return name;
    }

}
