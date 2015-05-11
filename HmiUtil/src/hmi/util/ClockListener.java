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

package hmi.util;

/**
 * A ClockListener can receive callbacks, in the form of
 * time(currentTime) calls, made by some Clock object.
 * Such callbacks will often arrive in more or less regular intervals,
 * and with varying precision, but no guarantees can be given in this respect.
 * @author Job Zwiers
 */
public interface ClockListener
{
   
   /**
    * initTime() is called before the Clock starts running, and sends some initial time value.
    * This will often equal the time send for the first regular time() call.
    * This is done on the same clock  Thread that is going to send the regular time() calls. 
    */
   void initTime(double initTime);
   
   
    /**
     * time() is called, with the &quot;current time&quot; specified in seconds.
     */
    void time(double currentTime);   
}
