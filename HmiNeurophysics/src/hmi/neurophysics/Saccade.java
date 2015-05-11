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
package hmi.neurophysics;

/**
 * Saccade information.
 * From http://www.liv.ac.uk/~pcknox/teaching/Eymovs/params.htm<br>
 * see also http://en.wikipedia.org/wiki/Saccade
 * @author welberge
 *
 */
public final class Saccade
{
    private Saccade(){}
    /**
     * http://www.liv.ac.uk/~pcknox/teaching/Eymovs/params.htm<br>
     * For normal subjects the relationship between saccade amplitude and duration is fairly linear. 
     * The equation of the line through normal subject data is usually 2.2A+21 (where A is the saccade amplitude).
     */
    public static double getSaccadeDuration(double angle)
    {
        double angleDeg = Math.toDegrees(angle);
        return 0.0022*angleDeg;
    }
}
