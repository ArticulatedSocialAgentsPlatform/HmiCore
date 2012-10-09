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
