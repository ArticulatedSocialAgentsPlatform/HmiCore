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
