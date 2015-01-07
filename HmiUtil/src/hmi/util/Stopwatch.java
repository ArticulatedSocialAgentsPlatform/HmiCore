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
 * Stopwatch is a class for performing timing.
 * A Stopwatch starts running immediately upon creation.
 * It can at any time be reset to 0 by calling start() or reset().
 * The current stopwatch time is shown on the Console with show();
 * (The latter will not stop or reset the stopwatch; it keeps running)
 * If a series of timeintervals must be measured, it is possible to call
 * showLap() rather than show(). Consecutive showLap() calls show the time
 * between these calls, whereas show() always show the accumulated time.
 * Timing can also be done without Console output, by calling read() instead
 * of show(), or readLap() instead of showLap().
 * Finally, Stopwatch offers a delay(long) method, which has nothing to do with 
 * the stopwatch instances as such. It performs a Thread.sleep(), and catches the
 * annoying exception.
 * @author Job Zwiers
 */
public class Stopwatch  {


   /**
    * create a new numbered Stopwatch.
    * The name is of the form "Stopwatch <n>".
    * It starts running right away.
    */
   public Stopwatch() {
      this.name = "Stopwatch " + (stopwatchcount++);
      start();
   }

   /**
    * create a new named Stopwatch.
    * It starts running right away.
    */   
   public Stopwatch(String name) {
      this.name = name;
      start();
   }

   /**
    * returns the name of this Stopwatch.
    */
   public String getName() {
      return name;
   }

 
   /**
    * starts/resets the stopwatch, by reading the current system time.
    * The latter is returned.
    */   
   public void start() {
       starttime = System.nanoTime();
       lapstart = starttime;
   }


   /**
    * read the current time for the stopwatch, relative to the start time,
    * and show it on the hmi.util.Console, in seconds
    */
   public void showSeconds() {
      showSeconds("time (seconds) : ");
   }

   private static final int NANOSPERSECOND = 1000000000;
   private static final int NANOSPERMILLISECOND = 1000000;
   private static final int NANOSPERMICROSECOND = 1000;
   
   /**
    * read the current time for the stopwatch, relative to the start time,
    * and show it on the hmi.util.Console, in seconds
    */
   public final void showSeconds(String message) {
      currenttime = System.nanoTime();
      lapstart = currenttime;
      hmi.util.Console.println("[" + name + "] " + message + " " + ((currenttime-starttime)/NANOSPERSECOND));
   }

   /**
    * read the current time for the stopwatch, relative to the start time,
    * and show it on the hmi.util.Console, in seconds
    */
   public void showMilliseconds() {
      showMilliseconds("time (milliseconds) : ");
   }

   /**
    * read the current time for the stopwatch, relative to the start time,
    * and show it on the hmi.util.Console, in milliseconds
    */
   public final void showMilliseconds(String message) {
      currenttime = System.nanoTime();
      lapstart = currenttime;
      hmi.util.Console.println("[" + name + "] " + message + " " + ((currenttime-starttime)/NANOSPERMILLISECOND));
   }

   /**
    * read the current time for the stopwatch, relative to the start time,
    * and show it on the hmi.util.Console, in microseconds
    */
   public final void showMicroseconds() {
      currenttime = System.nanoTime();
      lapstart = currenttime;
      hmi.util.Console.println("[" + name + "] time (microseconds) : " + ((currenttime-starttime)/NANOSPERMICROSECOND));
   }


   /**
    * read the current time for the stopwatch, relative to the start time,
    * and show it on the hmi.util.Console, in microseconds
    */
   public final void showMicroseconds(String message) {
      currenttime = System.nanoTime();
      lapstart = currenttime;
      hmi.util.Console.println("[" + name + "] " + message + " " + ((currenttime-starttime)/NANOSPERMICROSECOND));
   }
  
    /**
    * read the current time for the stopwatch, relative to the start time,
    * and show it on the hmi.util.Console, in nanoseconds.

    */
   public final void showNanoseconds(String message) {
      currenttime = System.nanoTime();
      lapstart = currenttime;
      hmi.util.Console.println("[" + name + "] " + message + " " + (currenttime-starttime));
   }
   
   public final void showNanoseconds() {
      currenttime = System.nanoTime();
      lapstart = currenttime;
      hmi.util.Console.println("[" + name + "] time (nanoseconds) : " + (currenttime-starttime));
   }

   /**
    * read the "lap" time for the stopwatch, relative to reading the last lap time,
    * or relative to starttime if this is the first readLap call.
    * and show it on the hmi.util.Console.
    * The lap time is in milliseconds. 
    * (There is no microseconds or even nanoseconds version
    * of showLap, since the Console.print action takes milliseconds itself).
    */
   public long showMillisecondsLap() {
      currenttime = System.nanoTime();
      long lap = ((currenttime-lapstart)/NANOSPERMILLISECOND);
      hmi.util.Console.println("[" + name + "] lap time (milliseconds): " + lap );
      lapstart = currenttime;
      return lap;
   }

 

   /**
    * same as Thread.sleep(), but catches and ignores Exceptions.
    */
   public static void delay(long d) {
      try {
          Thread.sleep(d);
      } catch (Exception e) {
         System.err.println("Stopwatch.delay: " + e);   
      }
   }

   private static final int TESTDELAY1 = 2000;
   private static final int TESTDELAY2 = 3000;
   

   public static void main(String[] arg) {
      hmi.util.Console.println("Stopwatch test");
      Stopwatch watch1 = new Stopwatch("Watch1");
      watch1.start();
      Stopwatch.delay(TESTDELAY1);
      watch1.showMillisecondsLap();
      watch1.showMilliseconds();
      Stopwatch.delay(TESTDELAY2);
      watch1.showMilliseconds();
      watch1.showMillisecondsLap();
        
   }

   private long starttime = 0;
   private long currenttime = 0;
   private long lapstart = 0;
   private String name;
   private static int stopwatchcount = 1;
} 

