package hmi.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.*;

/**
 * Unit test for the RuntimeExceptionLoggingRunnable. 
 * A suitable slf4j logger should be in the classpath for this test to work. 
 * @author welberge
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(LoggerFactory.class)
public class RuntimeExceptionLoggingRunnableTest
{
    Logger mockLogger = mock(Logger.class);
    
    @Test
    public void test() throws InterruptedException
    {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        PowerMockito.mockStatic(LoggerFactory.class);
        when(LoggerFactory.getLogger(anyString())).thenReturn(mockLogger);        
        
        class Runner implements Runnable
        {
            @Override
            public void run()
            {
                throw new RuntimeException("RuntimeException!");
            }
        }
        exec.submit(new RuntimeExceptionLoggingRunnable(new Runner()));
        Thread.sleep(500);
        
        verify(mockLogger,times(1)).error(anyString(),(RuntimeException)any());                
    }
}
