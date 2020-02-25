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
