package hmi.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Test;

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
