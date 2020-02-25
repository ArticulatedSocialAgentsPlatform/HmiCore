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
package hmi.testutil.rules;

import org.junit.runners.model.Statement;

/**
 * Based on FailOnTimeout in JUnit. Inserts String information from the TimeoutCallback into the
 * timeout exception text.
 * @author welberge
 *
 */
public class FailOnTimeoutWithCallback extends Statement
{
    private Statement fNext;

    private final long fTimeout;

    private final TimeoutCallback timeoutCallback;

    private boolean fFinished = false;

    private Throwable fThrown = null;

    public FailOnTimeoutWithCallback(Statement next, long timeout, TimeoutCallback cb)
    {
        fNext = next;
        fTimeout = timeout;
        timeoutCallback = cb;
    }

    @Override
    public void evaluate() throws Throwable
    {
        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    fNext.evaluate();
                    fFinished = true;
                }
                catch (Throwable e)
                {
                    fThrown = e;
                }
            }
        };
        thread.start();
        thread.join(fTimeout);
        if (fFinished) return;
        if (fThrown != null) throw fThrown;
        Exception exception = new Exception(String.format("test timed out after %d milliseconds. Progress information: %s", fTimeout,
                timeoutCallback.getProgressInfo()));
        exception.setStackTrace(thread.getStackTrace());
        throw exception;
    }
}
