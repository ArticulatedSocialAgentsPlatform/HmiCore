/*
 * Diff JUnit test
 */

package hmi.util;

import org.junit.*;

/**
 * JUnit test for hmi.util.Diff
 */
public class DiffTest {
    
    public DiffTest() {
    }

    @Before
    public void setUp()  { // common initialization, executed for every test.
    }

    @After
    public void tearDown() {
    }

    @Test
    public void diffTest() {       
       int[] a0 = null; int[] b0 = null;
       int[] a1 = new int[] { 1, 2, 3};
       int[] b1 = new int[] { 1, 4, 5};
       String d1 = Diff.showDiff("a0/b0", a0, b0);
       //System.out.println("d1=" + d1);
       
       String d2 = Diff.showDiff("a1/b0", a1, b0);
       //System.out.println("d2=" + d2);
       
       String d3 = Diff.showDiff("a1/b1", a1, b1);
       //System.out.println("d3=" + d3);
       
       
       
       String d4 = Diff.showDiff("a1/a1", a1, a1);
       //System.out.println("d4=" + d4);
       
       
       float[] f1 = new float[] {1.0f, 2.0f, 3.0f, 4.0f};
       float[] f2 = new float[] {1.0f, 2.0f, 3.0f};
       
       String d5 = Diff.showDiff("f1/f2", f1, f2);
       //System.out.println("d5=" + d5);
       
       String[] s1 = new String[] {"aap", "noot", "mies"};
       String[] s2 = new String[] {"aap", "noot", "nies"};
       String d6 = Diff.showDiff("s1/s2", s1, s2);
       //System.out.println("d6=" + d6);
       
       
     
    } 

  
}
