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

import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * Monitors the AWT event dispatch thread for events that take longer than
 * a certain time to be dispatched.
 * 
 * The principle is to record the time at which we start processing an event,
 * and have another thread check frequently to see if we're still processing.
 * If the other thread notices that we've been processing a single event for
 * too long, it prints a stack trace showing what the event dispatch thread
 * is doing, and continues to time it until it finally finishes.
 * 
 * This is useful in determining what code is causing your Java application's
 * GUI to be unresponsive.
 * 
 * @author Elliott Hughes <enh@jessies.org>
 */
public final class EventDispatchThreadHangMonitor extends EventQueue {
    private static final EventQueue INSTANCE = new EventDispatchThreadHangMonitor();
    
    // Time to wait between checks that the event dispatch thread isn't hung.
    private static final long CHECK_INTERVAL_MS = 100;
    
    // Maximum time we won't warn about.
    private static final long UNREASONABLE_DISPATCH_DURATION_MS = 500;
    
    // Used as the value of startedLastEventDispatchAt when we're not in
    // the middle of event dispatch.
    private static final long NO_CURRENT_EVENT = 0;
    
    // When we started dispatching the current event, in milliseconds.
    private long startedLastEventDispatchAt = NO_CURRENT_EVENT;
    
    // Have we already dumped a stack trace for the current event dispatch?
    private boolean reportedHang = false;
    
    // The event dispatch thread, for the purpose of getting stack traces.
    private Thread eventDispatchThread = null;
    
    
    public synchronized long getStartedLastEventDispatchAt() { return startedLastEventDispatchAt; } // added to avoid sync problems. JZ
    public synchronized Thread getEventDispatchThread() { return eventDispatchThread; }
    public synchronized boolean getReportedHang() { return reportedHang; }
    public synchronized void setReportedHang(boolean val) { reportedHang = val; }
    
    private EventDispatchThreadHangMonitor() {
        initTimer();
    }
    
    /**
     * Sets up a timer to check for hangs frequently.
     */
    private void initTimer() {
        final long initialDelayMs = 0;
        final boolean isDaemon = true;
        Timer timer = new Timer("EventDispatchThreadHangMonitor", isDaemon);
        timer.schedule(new HangChecker(), initialDelayMs, CHECK_INTERVAL_MS);
    }
    
    /**
     * TimerTask that executes checks for hanging
     */
    private class HangChecker extends TimerTask {
        @Override
        public void run() {
            // Synchronize on the outer class, because that's where all
            // the state lives.
            synchronized (INSTANCE) {
                checkForHang();
            }
        }
        
        private void checkForHang() {
            if (getStartedLastEventDispatchAt() == NO_CURRENT_EVENT) {
                // We don't destroy the timer when there's nothing happening
                // because it would mean a lot more work on every single AWT
                // event that gets dispatched.
                return;
            }
            if (timeSoFar() > UNREASONABLE_DISPATCH_DURATION_MS) {
                reportHang();
            }
        }
        
        private  void reportHang() { 
            if (getReportedHang()) {
                // Don't keep reporting the same hang every 100 ms.
                return;
            }
            
            setReportedHang(true);
            
            System.out.println("--- event dispatch thread stuck processing event for " +  timeSoFar() + " ms:");
            StackTraceElement[]  stackTrace = getEventDispatchThread().getStackTrace();
            printStackTrace(System.out, stackTrace);
        }
        
        private void printStackTrace(PrintStream out, StackTraceElement[] stackTrace) {
            // We know that it's not interesting to show any code above where
            // we get involved in event dispatch, so we stop printing the stack
            // trace when we get as far back as our code.
            final String ourEventQueueClassName = EventDispatchThreadHangMonitor.class.getName();
            for (StackTraceElement stackTraceElement : stackTrace) {
                if (stackTraceElement.getClassName().equals(ourEventQueueClassName)) {
                    return;
                }
                out.println("    " + stackTraceElement);
            }
        }
    }
    
    /**
     * Returns how long we've been processing the current event (in
     * milliseconds).
     */
    private long timeSoFar() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - startedLastEventDispatchAt);
    }
    
    /**
     * Sets up hang detection for the event dispatch thread.
     */
    public static void initMonitoring() {
        Toolkit.getDefaultToolkit().getSystemEventQueue().push(INSTANCE);
    }
    
    /**
     * Overrides EventQueue.dispatchEvent to call our pre and post hooks either
     * side of the system's event dispatch code.
     */
    @Override
    protected void dispatchEvent(AWTEvent event) {
        preDispatchEvent();
        super.dispatchEvent(event);
        postDispatchEvent();
    }
    
    /**
     * Stores the time at which we started processing the current event.
     */
    private synchronized void preDispatchEvent() {

       if (getEventDispatchThread() == null) {
          // I don't know of any API for getting the event dispatch thread,
          // but we can assume that it's the current thread if we're in the
          // middle of dispatching an AWT event...
          eventDispatchThread = Thread.currentThread();
       }
        
        setReportedHang(false);
        startedLastEventDispatchAt = System.currentTimeMillis();
    }
    
    /**
     * Reports the end of any ongoing hang, and notes that we're no longer
     * processing an event.
     */
    private synchronized void postDispatchEvent() {
        if (getReportedHang()) {
            System.out.println("--- event dispatch thread unstuck after " + timeSoFar() + " ms.");
        }
        startedLastEventDispatchAt = NO_CURRENT_EVENT;
    }
}
