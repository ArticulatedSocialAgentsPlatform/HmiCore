/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2015 University of Twente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package hmi.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

/**
 * Unit tests for the collectionutils
 * @author hvanwelbergen
 *
 */
public class CollectionUtilsTest
{
    @Test
    public void testEnsureSizeFromEmptyList()
    {
        List<Double> list = new ArrayList<>();
        CollectionUtils.ensureSize(list, 10);
        assertEquals(10, list.size());
    }
    
    @Test
    public void getFirstClassOfTypeTest()
    {
        class A{};
        class B extends A{};
        class C extends B{};
        class D extends A{};
        C c = new C();
        List<A> list = ImmutableList.of(new A(), c, new B(), new D());
        assertEquals(c, CollectionUtils.getFirstClassOfType(list, B.class));
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
        List<A> list = ImmutableList.of(new A(), c, b, new D());
        assertThat(CollectionUtils.getClassesOfType(list,B.class), IsIterableContainingInAnyOrder.containsInAnyOrder(c,b));
    }
    
}
