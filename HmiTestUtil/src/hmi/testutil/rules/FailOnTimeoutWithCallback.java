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
