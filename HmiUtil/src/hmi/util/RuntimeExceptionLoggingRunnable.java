package hmi.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wraps a try/catch block around a Runnable and logs any RuntimeExceptions that might occur 
 * during the run of that Runnable. The exception is logged and then re-thrown.
 * Useful for the ExecutorService framework, since there is no way to obtain its exceptions 
 * from threads that don't stop running.
 * @author Herwin
 */
public class RuntimeExceptionLoggingRunnable implements Runnable
{
    private final Runnable runnable;
    /**
     * 
     * @param r
     */
    public RuntimeExceptionLoggingRunnable(Runnable r)
    {
        runnable = r;
    }

    @Override
    public void run()
    {
        try
        {
            runnable.run();
        }        
        catch (RuntimeException e)
        {
            Logger logger = LoggerFactory.getLogger(runnable.getClass().getName());            
            logger.error("Exception captured by ExceptionLoggingRunnable: ",e);
            throw e;
        }        
    }
}
