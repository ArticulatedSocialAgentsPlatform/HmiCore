//
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

//
package hmi.util;

/**
 * A Clock is an object that delivers time stamps at 
 * (more or less) regular intervals to a set of subscribers.
 * The time is delivered in the form of a "long" integer, in milliseconds.
 * The interval between two clock "ticks" and the precision of the time value itself 
 * are not guaranteed, and are system dependent.
 * @author Job Zwiers
 */
public interface Clock {
   
   /**
    * adds "listener" to the collection of ClockListeners
    * that receive time(currentTime) callbacks.
    */
   void addClockListener(ClockListener listener);
   
   /**
    * Gets this Clock's current media time in seconds. 
    */
   double getMediaSeconds();
    
}
