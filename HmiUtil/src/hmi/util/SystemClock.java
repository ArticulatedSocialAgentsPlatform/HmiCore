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

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * How to use in practice:
 * <ol>
 * <li>Allocate a SystemClock object, say &quot;clock &quot;</li>
 * <li>Add one or more ClockListeners by means of the addClockListener() method. Each ClockListener must implement the ClockListener interface, which
 * means that it must have a method of type public void time(double currentTime).</li>
 * <li>When desired, the clock can send a first time() call to all listeners, without starting the clock running, by means of the initMediaTime()
 * method.</li>
 * <li>Start the clock running by calling clock.start() The clock will start a new Thread, that continuously determines the current system time in
 * seconds, and then calls the time(currentTime) method of each registered ClockListener.</li>
 * <li>The clock can be paused and (re)started by means of the pause and start methods. When restarting a paused clock, the media time, send to the
 * ClockListeners, will resume at the time when the clock was paused.</li>
 * <li>When the clock is paused, it is possible to perform manual clock steps, where the media time is increased with user specified amounts. The
 * time() calls broadcasted to the clock listeners are executed from the clock Thread.</li>
 * <li>Before (re)starting, or even while the clock is running, the media time can be set to some desired value, by calling methods like
 * setMediaSeconds(double mediatime). When no value is set, the media time starts off at 0 when the clock is started for the first time.</li>
 * <li>Usually, the media time progresses at the rate as the system time. The setRate() method can be used to set a rate factor different from 1.0, in
 * order to speed up, slow down, or, in the case of negative value, reverse media time.</li>
 * <li>The tick size, in milliseconds, specifies the desired delay between two clock ticks. Due to hardware dependencies and OS problems, the actual
 * delay might be much larger, and might be erratic, especially for tick sizes below 16 ms.</li>
 * <li>When the clock is started with tick size 0, or without specifying the tick size, then the clock runs at &quot;full speed&quot;, and the delay
 * between clock ticks will be determined by the workload caused by the ClockListeners.</li>
 * <li>It is possible to show a &quot;frame rate&quot; counter in the title bar of some Java Frame or JFrame, by calling the
 * setFramerateCounterFrame(frame) method.</li>
 * </ol>
 * 
 * @author Job Zwiers
 */
public class SystemClock implements Clock
{

    
    private Thread clockThread; // The Java Thread running the "clock" when "ticking".
    private long nanoTickSize = 0; // approximate delay between clock ticks, in nanoseconds.

    private long currentTimeBaseTime = System.nanoTime();
    private volatile long mediaTime; // current media time, in nanoseconds.
    private volatile double rate = 1.0; // clock rate: determines ratio between system time rate and media time rate.

    private volatile boolean listenersModified = false; // flag, denoting whether new clock listeners have been added
    private ArrayList<ClockListener> listeners = new ArrayList<ClockListener>(); // list of clock listeners
    private ArrayList<ClockListener> newListeners = new ArrayList<ClockListener>(); // list of added clock listeners that will be added to listeners
   
    private final static int INIT = 0; // constant denoting the initial clock state, when no clock Thread is running
    private final static int RUNNING = 1; // constant denoting the state where the clock Thread is running, and sendinding clock ticks (by calling
                                          // time())
    private final static int PAUSED = 2; // constant denoting the state where the clock Thread is still active, but waiting.
    private final static int TERMINATED = 3; // constant denoting the terminated state where the clock Thread is terminating.
    private volatile int clockState = INIT;


    private Semaphore runSem = new Semaphore(0);
    private String threadName = null;
    
    private static final long  NANOSPERSMILLISECOND = 1000000L;
    private static final double NANOSPERSECOND = 1E9;
    private static final double MILLISPERSECOND = 1E3;

    
    /**
     * Create a new SystemClock with tickSize == 0 (i.e. run as fast as possible)
     */
    public SystemClock()
    {
        this(0);
    }

    /**
     * Create a new SystemClock with specified tickSize and null Clock-Thread name
     */
    public SystemClock(long tickSize)
    {
        this(tickSize,null);
    }
    
    
    public SystemClock(String threadName)
    {
        this(0,threadName);
    }
    
    /**
     * Creates a new SystemClock with specified tickSize and specified Clock-Thread name.
     * The Clock is not yet &quot;ticking&quot;, with mediaTime set to 0.
     */
    public SystemClock(long tickSize, String threadName)
    {
        this.threadName = threadName;
        mediaTime = 0;
        clockState = INIT;
        this.nanoTickSize = tickSize * NANOSPERSMILLISECOND;
    }


    /**
     * Adds "listener" to the list of ClockListeners that receive initTime() and time() callbacks.
     */
    @Override
    public void addClockListener(ClockListener listener)
    {
        synchronized (newListeners)
        {
            newListeners.add(listener);
            listenersModified = true;
        }
    }

    /* Transfer new Clocklisteners from newListeners to  listeners */
    private void updateListeners()
    {
        if (listenersModified)
        {
            synchronized (newListeners)
            {
                listeners.addAll(newListeners);
                newListeners.clear();
                listenersModified = false;
            }
        }
    }

    /**
     * Gets this Clock's current media time in seconds. 
     * The result is a double, obtained by casting the internal long nanosecond representation.
     */
    @Override
    public synchronized double getMediaSeconds()
    {
        return getMediaNanoseconds() / NANOSPERSECOND;
    }

    /**
     * Sets the Clock's media time, specified in second. 
     * Note that the time is specified as a double; this is converted to a long, 
     * representing the time in nanoseconds. 
     * When the clock is already RUNNING, the effect is that the clock &apos;skips&apos; instantly to the new time.
     */
    public synchronized void setMediaSeconds(double mt)
    {
        mediaTime = (long) (mt * NANOSPERSECOND);
    }

    /**
     * Gets this Clock's current media time in nanoseconds, specified by a long.
     */
    public long getMediaNanoseconds()
    {
        return mediaTime;
    }


    /**
     * Start running, i.e. start &apos;ticking&apos; by performing time(currentTime) callbacks. 
     * This method can be called whenever the Clock is in its initial state, or when it is paused.
     * A Clock that has terminated cannot be started again. 
     */
    public synchronized void start()
    {
        if (clockState == TERMINATED || clockState == RUNNING)
            return;
        if (clockState == INIT)
        {
            clockState = RUNNING;
            startClockThread();
        }
        else
        { // clockState == PAUSED
            clockState = RUNNING;
        }
        runSem.release();
    }

    /**
     * Put the clock in the paused state. In this state, the clock Thread is waiting, but not terminated. 
     * The clock can resume ticking by calling the start() method again.
     * It is allowed to call pause when the clock is not yet running; in that case the clock Thread will be
     * created, but starts waiting.
     */
    public synchronized void pause()
    {
        if (clockState == TERMINATED || clockState == PAUSED)
            return;
        if (clockState == INIT)
        {
            clockState = PAUSED;
            startClockThread(); // do not release runSem. This will have to be done by a later start() call
        }
        else
        {
            clockState = PAUSED;
        }
    }

    /**
     * Will stop the clock and will terminate the clock Thread.
     */
    public synchronized void terminate()
    {
        if (clockState == TERMINATED) return;
        clockState = TERMINATED;
    }

    /**
     * Initializes the media time, broadcasts this media time to all clock listeners and puts the clock in the paused state, ready to start
     */
    public synchronized void init()
    {
        if (clockState != INIT) return;
        pause();
    }


    /**
     * Sets the temporal scale factor. The clock rate determines how fast the media time changes relative to the underlying time base time. The
     * default clock rate is 1.0. A negative rate is allowed, and results in mediaTime runing &quot;backwards&quot; A zero rate is allowed but,
     * although media time will no longer increase, the clock remains in the RUNNING state. It is allowed to (re)set the clock rate while it is
     * RUNNING already.
     */
    public synchronized void setRate(double rate)
    {
        this.rate = rate;
    }

    /**
     * Gets the current temporal scale factor.
     */
    public synchronized double getRate()
    {
        return rate;
    }



    /*
     * Forwards the current media time (currentMediaTime), converted to seconds, to all registered ClockListeners. 
     * When new clockListeners have been registered, they are added to the list of ClockListeners at this moment.
     */
    private void initTime()
    {
        updateListeners();
        double curTime = mediaTime / NANOSPERSECOND;
        for (ClockListener cl : listeners)
        {
            cl.initTime(curTime);
        }
    }

    /*
     * Forwards the current media time (currentMediaTime), converted to seconds, to all registered ClockListeners. 
     * When new clockListeners have been registered, they are added to the list of ClockListeners at this moment.
     */
    private void time()
    {
        updateListeners();
        double curTime = mediaTime / NANOSPERSECOND;
        for (ClockListener cl : listeners)
        {
            cl.time(curTime);
        }
    }

    // independent Thread that drives the clock
    class ClockThread extends Thread
    {
        public ClockThread(String threadName)
        {
            super(threadName);
        }

        public ClockThread()
        {
            super();
        }

        @Override
        public void run()
        {
            try
            {
                long prevTimeBaseTime = currentTimeBaseTime;
                initTime();
                while (clockState != TERMINATED)
                {
//                    resetFrameRate();
                    runSem.acquire();
                    currentTimeBaseTime = System.nanoTime();
                    while (clockState == RUNNING)
                    {
                        time();
 //                       reportFrameRate();
                        long delta = currentTimeBaseTime + nanoTickSize - System.nanoTime();
                        if (delta <= 0)
                        {
                            Thread.yield();
                        }
                        else
                        {
                            Thread.sleep(delta / NANOSPERSMILLISECOND);
                        }
                        prevTimeBaseTime = currentTimeBaseTime;
                        currentTimeBaseTime = System.nanoTime();
                        long timeBaseDelta = currentTimeBaseTime - prevTimeBaseTime;
                        long mediaTimeDelta = (rate == 1.0) ? timeBaseDelta : (long) (rate * timeBaseDelta);
                        mediaTime += mediaTimeDelta;
                    }
                }
            }
            catch (InterruptedException ie)
            {
                clockState = TERMINATED;
            } // just stop running when interrupted
        }
    }
    
    /**
     * Creates and starts a Thread that delivers clock ticks, by calling tick() on a regular base.
     */
    private void startClockThread()
    {
        if(this.threadName==null)
        {
            clockThread = new ClockThread();
        }
        else
        {
            clockThread = new ClockThread(threadName);
        }
        clockThread.start();
    }

}
