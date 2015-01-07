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
/**
 * @author Elliott Hughes <enh@jessies.org> (?)
 */
package hmi.util;

import java.lang.management.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Some class for deadlock detection
 * @author Elliott Hughes <enh@jessies.org> (?)
 */
public class ThreadDeadlockDetector {
  private final Timer threadCheck =
      new Timer("ThreadDeadlockDetector", true);
  private final ThreadMXBean mbean =
      ManagementFactory.getThreadMXBean();
  private final Collection<Listener> listeners =
      new CopyOnWriteArraySet<Listener>();

  /**
   * The number of milliseconds between checking for deadlocks.
   * It may be expensive to check for deadlocks, and it is not
   * critical to know so quickly.
   */
  private static final int DEFAULT_DEADLOCK_CHECK_PERIOD = 10000;

  public ThreadDeadlockDetector() {
    this(DEFAULT_DEADLOCK_CHECK_PERIOD);
  }

  private static final long TIMERDELAY = 10;

  public ThreadDeadlockDetector(int deadlockCheckPeriod) {
    threadCheck.schedule(new TimerTask() {
      public void run() {
        checkForDeadlocks();
      }
    }, TIMERDELAY, deadlockCheckPeriod);
  }

  private void checkForDeadlocks() {
    long[] ids = findDeadlockedThreads();
    if (ids != null && ids.length > 0) {
      Thread[] threads = new Thread[ids.length];
      for (int i = 0; i < threads.length; i++) {
        threads[i] = findMatchingThread(
            mbean.getThreadInfo(ids[i]));
      }
      fireDeadlockDetected(threads);
    }
  }

  private long[] findDeadlockedThreads() {
    // JDK 1.5 only supports the findMonitorDeadlockedThreads()
    // method, so you need to comment out the following three lines
    if (mbean.isSynchronizerUsageSupported())
      return mbean.findDeadlockedThreads();
    else
      return mbean.findMonitorDeadlockedThreads();
  }

  private void fireDeadlockDetected(Thread[] threads) {
    for (Listener l : listeners) {
      l.deadlockDetected(threads);
    }
  }

  private Thread findMatchingThread(ThreadInfo inf) {
    for (Thread thread : Thread.getAllStackTraces().keySet()) {
      if (thread.getId() == inf.getThreadId()) {
        return thread;
      }
    }
    throw new IllegalStateException("Deadlocked Thread not found");
  }

  public boolean addListener(Listener l) {
    return listeners.add(l);
  }

  public boolean removeListener(Listener l) {
    return listeners.remove(l);
  }

  /**
   * This is called whenever a problem with threads is detected.
   */
  public interface Listener {
    void deadlockDetected(Thread[] deadlockedThreads);
  }
}
