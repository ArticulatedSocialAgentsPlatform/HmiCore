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
