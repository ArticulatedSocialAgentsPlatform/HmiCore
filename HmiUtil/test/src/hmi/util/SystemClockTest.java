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

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the systemclock
 * @author hvanwelbergen
 *
 */
public class SystemClockTest
{
    SystemClock sysClock;
    
    @Before
    public void setup()
    {
        sysClock = new SystemClock();
    }
    
    @After
    public void tearDown()
    {
        sysClock.terminate();
    }
    
    @Test
    public void testRate() throws InterruptedException
    {
        sysClock.start();
        sysClock.setRate(2);                
        Thread.sleep(2000);        
        assertEquals(4,sysClock.getMediaSeconds(),0.4);
    }
}
