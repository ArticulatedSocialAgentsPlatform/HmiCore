/*******************************************************************************
 * Copyright (C) 2009-2020 Human Media Interaction, University of Twente, the Netherlands
 *
 * This file is part of the Articulated Social Agents Platform BML realizer (ASAPRealizer).
 *
 * ASAPRealizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ASAPRealizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ASAPRealizer.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
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
