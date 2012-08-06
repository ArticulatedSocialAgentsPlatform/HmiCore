/*
 * Bezier JUnit test
 */

package hmi.math;

import org.junit.*;


/**
 * JUnit test for hmi.math.Bezier
 */
public class Bezier1fTest {
    
    public Bezier1fTest() {
    }

    @Before
    public void setUp()  { // common initialization, executed for every test.
    }

    @After
    public void tearDown() {
    }

    @Test
    public void basics() {       
       float[] px = new float[] {0.0f, 2.0f, 3.0f, 6.0f, 9.0f, 10.0f, 15.0f};
       float[] py = new float[] {0.0f, 2.0f, 2.0f, 0.0f, -2.0f, 4.0f, 0.0f};
       Bezier1f bezx = new Bezier1f(px);
       Bezier1f bezy = new Bezier1f(py);
       System.out.println("bezx=" + bezx);
       System.out.println("bezy=" + bezy);
      bezx.eval(0.0f);     bezy.eval(0.0f);
      bezx.eval(0.4f);     bezy.eval(0.4f);
      bezx.eval(0.5f);     bezy.eval(0.5f);
      bezx.eval(0.8f);     bezy.eval(0.8f);
      bezx.eval(1.0f);     bezy.eval(1.0f);
       
      bezx.evalInverse(0.001f); 
      bezx.evalInverse(4.416f); 
      bezx.evalInverse(6.0f); 
      bezx.evalInverse(10.535999f); 
      bezx.evalInverse(14.999f); 
       
       
    } 

  
}

