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
