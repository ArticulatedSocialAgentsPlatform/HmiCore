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

import hmi.util.rtsimockups.RTSIMockup1;
import hmi.util.rtsimockups.RTSIMockup2;
import hmi.util.rtsimockupsjar.RTSIMockupJar2;
import hmi.util.rtsimockupsjar.RTSIMockupJar1;

import java.util.Set;

import org.junit.Test;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.collection.*;

/**
 * Unit testcases for FindClass
 * @author welberge
 */
public class FindClassTest
{
    @SuppressWarnings("unchecked")
    @Test
    public void testFindClasses()
    {
        FindClass fc = new FindClass();
        Set<Class<?>> classes = fc.findClasses();
        assertThat(classes, hasItems(RTSIMockup1.class,RTSIMockup2.class,FindClassTest.class));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testFindClassesInJar()
    {
        FindClass fc = new FindClass();
        Set<Class<?>> classes = fc.findClasses();
        assertThat(classes, hasItems(RTSIMockupJar1.class,RTSIMockupJar2.class));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testFindClassesInPackage()
    {
        FindClass fc = new FindClass();
        Set<Class<?>> classes = fc.findClasses("hmi.util.rtsimockups");
        assertThat(classes,IsIterableContainingInAnyOrder.containsInAnyOrder(RTSIMockup1.class,RTSIMockup2.class));        
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testFindClassesInJarAndPackage()
    {
        FindClass fc = new FindClass();
        Set<Class<?>> classes = fc.findClasses("hmi.util.rtsimockupsjar");
        assertThat(classes,IsIterableContainingInAnyOrder.containsInAnyOrder(RTSIMockupJar1.class,RTSIMockupJar2.class));               
    }
}
