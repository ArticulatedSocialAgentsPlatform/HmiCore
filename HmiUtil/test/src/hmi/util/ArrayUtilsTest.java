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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Test;

/**
 * Unit tests for ArrayUtils
 * @author Herwin
 *
 */
public class ArrayUtilsTest
{
    @Test
    public void getFirstClassOfTypeTest()
    {
        class A{};
        class B extends A{};
        class C extends B{};
        class D extends A{};
        C c = new C();
        A[] list = {new A(), c, new B(), new D()};
        assertEquals(c, ArrayUtils.getFirstClassOfType(list, B.class));
    }
    
    @Test
    public void getClassesOfTypeTest()
    {
        class A{};
        class B extends A{};
        class C extends B{};
        class D extends A{};
        C c = new C();
        B b = new B();        
        A[] list = {new A(), c, b, new D()};
        assertThat(ArrayUtils.getClassesOfType(list,B.class), IsIterableContainingInAnyOrder.containsInAnyOrder(c,b));
    }
}
