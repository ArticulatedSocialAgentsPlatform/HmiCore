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

package hmi.util;

import java.awt.Frame;

/**
 * A FramerateCounter determines frame rate from clock ticks and  dsplays the &apos;current&apos; framerate
 * in the title bar of some Frame or JFrame. Optionally it also show the &apos;current&apos; media time,
 * derived from the ClockListener callbacks. (The framerate is determined independently from the
 * media time reported in callbacks)
 * @author Job Zwiers
 */
public class FramerateCounter implements ClockListener
{
   
   /**
    * Creates a new FramerateCounter that show framerates in the title bar of 
    * the specified Frame (which could be a JFrame)
    */
   public FramerateCounter(Frame frameRatecounterFrame) 
   {
      this.frameRatecounterFrame = frameRatecounterFrame;
   }
   
   /**
    * Sets the framerate counter text, in front of the actual frame rate number. (By default this is the text &quot;FPS: &quot;)
    */
   public void setFramerateCounterText(String prefixText)
   {
       this.framerateCounterText = prefixText;
   }
   
   /**
    * Sets the Time text, in front of the actual time display. (By default this is the text &quot;Time: &quot;)
    */
   public void setTimeText(String prefixText)
   {
       this.timeText = prefixText;
   }



   /**
    * ClockListener callback. Initializes the framerate counter.
    */
   public void initTime(double time) 
   {     
      currentTimeBaseTime = System.nanoTime();
      prevFramerateTime = currentTimeBaseTime;
      fpsCounter = 0;
      int secs = (int) (time);
      if ( ! displayFramerate && ! displayTime) return;
      if (displayFramerate) 
      {
         if (displayTime) 
         {
            frameRatecounterFrame.setTitle(framerateCounterText + "--" + "  " + timeText + secs);
         }
         else // displayFramerate && ! displayTime
         {
            
            frameRatecounterFrame.setTitle(framerateCounterText + "--");
         }        
      }
      else if (displayTime) // && ! displayFramerate
      {
         frameRatecounterFrame.setTitle(timeText + secs);
      }   
      
   }

   /**
    * ClockListener callback. Displays the current frame rate.
    */   
   public void time(double time)
   {
      if ( ! displayFramerate && ! displayTime) return;
      currentTimeBaseTime = System.nanoTime();   
      int secs = (int) (time);  
      if (! displayFramerate)  // && displayTime
      {
         frameRatecounterFrame.setTitle(timeText + secs);     
      }
      else { // displayFramerate      
         fpsCounter++;
         double fpsDelta = (currentTimeBaseTime - prevFramerateTime) / NANOSPERSECOND;
         if (fpsDelta >= refreshDelay)
         {
            int fps = (int) (fpsCounter / fpsDelta);
            if (displayTime)
            {
               frameRatecounterFrame.setTitle(framerateCounterText + fps +"  " + timeText + secs);   
            }
            else 
            {
               frameRatecounterFrame.setTitle(framerateCounterText + fps);   
            }
            prevFramerateTime = currentTimeBaseTime;
            fpsCounter = 0;
         
          //  if (displayText != null) frameRatecounterFrame.setTitle(framerateCounterText + displayText);
            
            
         }
      }
      
   }
   
   /**
    * Sets the delay between time reading refreshes for this FramerateCounter
    */
   public void setRefreshDelay(double rd)
   {
      refreshDelay = rd;
   }

   
   
   /**
    * Sets the framerate reporting mode. Default is true.
    */
   public void setDisplayFramerate(boolean mode)
   {
      displayFramerate = mode; 
   }
   
   /**
    * Sets the media time reporting mode. Default is true.
    */
   public void setDisplayTime(boolean mode)
   {
      displayTime = mode; 
   }
   
   
    private static final int TESTTICKSIZE = 100;
    private static final int DELAY1 = 3000;
    private static final int DELAY2 = 3000;
    private static final int DELAY3 = 2000;
    private static final int DELAY4 = 3000;
    private static FramerateCounter framerateCounter;


    /* test */
    public static void main(String[] arg)
    {
        Console.println("SystemClock test");

        // private static final long TICKTIMESIZE = 10000;
        

        ClockListener cl = new ClockListener()
        {
            private int count = 0;
            
            // private int limit = 10;
            // private long[] ticktime = new long[TICKTIMESIZE];
            public void initTime(double t)
            {
                count = 0;
            }

            public void time(double t)
            {
                long timeMillis = (long) (t * MILLISPERSECOND);
                // long timeMicros = (long) (t*MICROSPERSECOND);
                // if (count >= 99999) {
                // if (count >= 9999) {
                // if (count >= 99) {
                if (count >= 0)
                {
                    Console.println("time: " + t + " millis: " + timeMillis + "   ");
                    // for (int i=0; i<limit; i++) {
                    // Console.print(" # " + ticktime[i]);
                    // }
                    // Console.println();
                    count = 0;
                }
                // ticktime[count] = timeMicros;
                count++;
            }

        };

        // Console.println("test ready");

        SystemClock clock = new SystemClock(TESTTICKSIZE);
        framerateCounter = new FramerateCounter(hmi.util.Console.getFrame());
        framerateCounter.setDisplayTime(true);
        framerateCounter.setDisplayFramerate(true);
//        clock.setFramerateCounterFrame(hmi.util.Console.getFrame());
        clock.addClockListener(framerateCounter);

        clock.addClockListener(cl);

        // clock.startClockThread(14);
        // clock.start();
        // small delays cause erratic step sizes, depending on hardware.
        // a 16 millisec step seems to be ok
        // 1 ms --> 2
        // 2 ms -> 3
        // 4 ms --> 4
        // when using a 0 delay, there is no Thread.sleep(), and the delay becomes
        // something like 1-2 MICRO seconds. (probably the time it takes for a System.nanoTime())
        // clock.setMediaSeconds(3.0);
        clock.init();

        // hmi.util.Console.println("clock paused-1");
        // Console.delay(1000);
        // clock.stepMediaTimeMilliseconds(7000);
        // Console.delay(1000);
        // clock.stepMediaTimeMilliseconds(8000);
        // // Console.delay(1000);
        // // clock.stepMediaTimeMilliseconds(5000);
        // //

        Console.delay(DELAY1);
        // hmi.util.Console.println("start clock-1");
        clock.start();
        Console.delay(DELAY2);
        clock.pause();
        clock.setMediaSeconds(0.0);
        hmi.util.Console.println("clock paused-2");
        Console.delay(DELAY3);
        hmi.util.Console.println("start clock-2");
        clock.start();
        Console.delay(DELAY4);
        // Console.delay(3000);

        clock.terminate();
        Console.println("Clock stopped");
        // Console.println("Clock restart after 4000");
        // Console.delay(4000);
        // clock.setRate(-0.2);
        // clock.setMediaSeconds(1.0);
        //
        // clock.start();

    }

   
   
   
   
   
   
   
   private static final double MILLISPERSECOND = 1E3;
   private static final double NANOSPERSECOND = 1E9;
   public static final double REFRESHDELAY = 0.2; // Approximate delay before framerate display is refreshed, in seconds.
   private long prevFramerateTime; // reading of time base time for the frame rate counter;
   private int fpsCounter; // counts nr of frames since last framerate report
   private double refreshDelay = REFRESHDELAY; // approximate delay before framerate display is refreshed, in seconds.
   
   long prevTimeBaseTime;
   long currentTimeBaseTime;
    
    
   private boolean displayFramerate = true;    // determines whether framerate will be shown.
   private boolean displayTime = true;         // determines whether the current time will be shown.
   private String framerateCounterText = "FPS: "; // Text used as prefix for the framerate display
   private String timeText = "Time: ";            // Text used as prefix for the time display
   private Frame frameRatecounterFrame; // Frame (or JFrame) used to show the framerate counter (in the title bar)

}
    