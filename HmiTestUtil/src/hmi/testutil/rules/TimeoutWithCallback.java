package hmi.testutil.rules;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * Based on the standard Timeout Rule in JUnit, but provides a callback function that is to be used
 * to obtain extra progress information (in String form).
 * 
 * @author welberge
 */
public class TimeoutWithCallback implements MethodRule
{
    private final int timoutms;

    private TimeoutCallback timeoutCallback;

    /**
     * @param millis
     *            the millisecond timeout
     */
    public TimeoutWithCallback(int millis, TimeoutCallback callback)
    {
        timoutms = millis;
        timeoutCallback = callback;
    }

    @Override
    public Statement apply(Statement base, FrameworkMethod method, Object target)
    {
        return new FailOnTimeoutWithCallback(base, timoutms, timeoutCallback);
    }

}
