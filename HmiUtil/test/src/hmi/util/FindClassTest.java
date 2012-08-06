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
