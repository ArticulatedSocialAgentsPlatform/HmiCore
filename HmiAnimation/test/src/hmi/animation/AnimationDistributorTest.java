/*
 * AnimationDistributor JUnit test
 */

package hmi.animation;

import static org.junit.Assert.*;
import org.junit.*;
import java.util.*;
import hmi.util.*;

/**
 * JUnit test for hmi.animation.AnimationDistributor
 */
public class AnimationDistributorTest {
    
    public AnimationDistributorTest() {
    }

    @Before
    public void setUp()  { // common initialization, executed for every test.
    }

    @After
    public void tearDown() {
    }

    @Test
    public void basics() {       
       AnimationDistributor ad = new AnimationDistributor();
       List<String> availableIds = ad.getAvailableSkeletonIds();
       assertTrue( ! availableIds.isEmpty());
       
       Skeleton noSkel1 = ad.getSkeleton();
       Skeleton noSkel2 = ad.getSkeleton("Armandia");
       assertTrue(noSkel1 != null);
       assertTrue(noSkel2 == null);
       
       
    } 



    @Test
    public void testAddSkeletons() {
      
//        SkeletonTest skTest = new SkeletonTest();
//        Skeleton skel1 = skTest.createSkeleton("skel1");
//        Skeleton skel2 = skTest.createSkeleton("skel2");
//        AnimationDistributor ad = new AnimationDistributor();
//        ad.addSkeleton(skel1);
//        ad.addSkeleton(skel2);
//        List<String> availableIds = ad.getAvailableSkeletonIds();
//        assertTrue(availableIds.size() == 2);
//        assertTrue(availableIds.get(0).equals("skel1"));
//        assertTrue(availableIds.get(1).equals("skel2"));
//        
//        Skeleton sk1 = ad.getSkeleton();
//        assertTrue(sk1 != null);
//        
//        Skeleton sk2 = ad.getSkeleton("skel2");
//        assertTrue(sk2 != null);
//        Skeleton sk3 = ad.getSkeleton("skel3");
//        assertTrue(sk3 == null);
        
         
      
    }
  
}
