package hmi.testutil.rules;

/**
 * Progress info callback for TimeoutWithCallback. Implementing methods provide some String 
 * that gives extra information on the progress of the test before it timed out. 
 * @author welberge
 */
public interface TimeoutCallback
{
    /**
     * Provides progress info for a timeout
     */
    String getProgressInfo();
}
