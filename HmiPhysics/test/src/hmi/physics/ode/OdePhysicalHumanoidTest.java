/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
package hmi.physics.ode;


import hmi.physics.AbstractPhysicalHumanoidTest;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.odejava.HashSpace;
import org.odejava.Odejava;
import org.odejava.Space;
import org.odejava.World;

/**
 * Unit test cases for the OdePhysicalHumanoid
 * @author hvanwelbergen
 *
 */
public class OdePhysicalHumanoidTest extends AbstractPhysicalHumanoidTest
{
    @BeforeClass
    public static void setUpOnce() throws Exception
    {
        Odejava.init();        
    }
    
    @Before
    public void setup()
    {
        World world = new World();
        Space space = new HashSpace();
        pHuman = new OdeHumanoid("pHuman", world, space);        
    }
    
    @AfterClass
    public static void tearDownOnce() throws Exception
    {
        Odejava.close();
    }
}
