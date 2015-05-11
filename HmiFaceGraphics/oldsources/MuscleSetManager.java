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

import hmi.graphics.render.GenericMesh;

import java.util.HashSet;

public class MuscleSetManager
{
    private static HashSet<MuscleSet> muscleSets = new HashSet<MuscleSet>();

    /**
     * \brief put a muscles settings object in the list of loaded muscle settings
     */
    public static void put(MuscleSet muscles)
    {
        muscleSets.add(muscles);
    }

    public static HashSet<MuscleSet> getValues()
    {
        return muscleSets;
    }

    /**
     * The combination of GenericMesh and MuscleSet name must be unique, otherwise the first found result will be returned
     * 
     * @param genericMesh
     * @param muscleSetName
     * @return
     */
    public static MuscleSet get(GenericMesh genericMesh, String muscleSetName)
    {
        for (MuscleSet muscleSet : muscleSets)
            if (genericMesh == muscleSet.getGenericMesh() && muscleSet.getName().equals(muscleSetName))
                return muscleSet;
        return null;
    }
}
