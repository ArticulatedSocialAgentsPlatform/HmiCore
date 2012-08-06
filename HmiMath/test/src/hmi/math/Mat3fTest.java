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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hmi.math;

import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author zwiers
 */
public class Mat3fTest {

    public Mat3fTest() {
    }
    
    public float[] m;
    public float[] mexpect;

   

    @Before
    public void setUp() {
      m = new float[9];
      mexpect = new float[9];
      
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of set method, of class Mat3f.
     */
    @Test
    public void set() {
    }

    /**
     * Test of setFromQuatScale method, of class Mat3f.
     */
    @Test
    public void setFromQuatScale() {
        float[] m = new float[9];
        float x = 0.2f; 
        float y = 0.3f; 
        float z = (float)Math.sqrt(1.0f - x*x - y*y);
        float phi = 3.0f;
        float[] aa = new float[4];
        Vec4f.set(aa, x, y, z, phi);
        
        float[] q = new float[4];
        Quat4f.setFromAxisAngle4f(q, aa);
        Mat3f.setFromQuatScale(m, q, 1.0f);
        float[] qq = new float[4];
        Quat4f.setFromMat3f(qq, m);
        assertTrue(Vec4f.epsilonEquals(q, qq, 0.001f));
        //System.out.println("q= " + Vec4f.toString(q));
       // System.out.println("qq=" + Vec4f.toString(qq));
//        System.out.println(Mat3f.toString(m));
    }

    /**
     * Test of setFromAxisAngleScale method, of class Mat3f.
     */
    @Test
    public void setFromAxisAngleScale() {
        float[] m = new float[9];
        float[] m1 = new float[9];
        float[] m2 = new float[9];
        float[] aa = {0.55f, 0.55f, 0.55f, 3.1415f};
        float q1[] = new float[4];
        Mat3f.setFromAxisAngleScale(m, aa, 1.0f);
//        System.out.println(Mat3f.toString(m));
        
        Vec4f.set(aa,0.5f, 0.3f, 0.4f, (float)Math.PI);
        Mat3f.setFromAxisAngleScale(m1, aa, 1f);
        Quat4f.setFromAxisAngle4f(q1, aa);        
        Mat3f.setFromQuatScale(m2, q1, 1f);
        assertTrue(Mat3f.epsilonEquals(m1, m2, 0.001f));
    }

    /**
     * Test of setZero method, of class Mat3f.
     */
    @Test
    public void setZero() {
    }

    /**
     * Test of setIdentity method, of class Mat3f.
     */
    @Test
    public void setIdentity() {
    }

    /**
     * Test of setElement method, of class Mat3f.
     */
    @Test
    public void setElement() {
    }

    /**
     * Test of getElement method, of class Mat3f.
     */
    @Test
    public void getElement() {
    }

    /**
     * Test of getRow method, of class Mat3f.
     */
    @Test
    public void getRow() {
    }

    /**
     * Test of getColumn method, of class Mat3f.
     */
    @Test
    public void getColumn() {
    }

    /**
     * Test of mul method, of class Mat3f.
     */
    @Test
    public void mul() {
    }


   public void showM() {
      System.out.println("m=\n" + Mat3f.toString(m));
   }

   public void showMexpect() {
      System.out.println("mexpect=\n" + Mat3f.toString(mexpect));
   }

    /**
     * Test of transform method, of class Mat3f.
     */
    @Test
    public void transform() {
    }
    
    
    @Test
    public void testFrom4x4() {
       float[] m4x4 = new float[16];
       Mat4f.set(m4x4, 
              1.0f,  2.0f,  3.0f,  4.0f,
              5.0f,  6.0f,  7.0f,  8.0f,
              9.0f, 10.0f, 11.0f, 12.0f,
              13.0f, 14.0f, 15.0f, 16.0f
       ); 
       float[] m3x3 = Mat3f.from4x4(m4x4);
       float[] mexpect = new float[9];
       Mat3f.set(mexpect, 
             1.0f,  2.0f,  3.0f, 
             5.0f,  6.0f,  7.0f, 
             9.0f, 10.0f, 11.0f
       ); 
       assertTrue(Mat3f.equals(m3x3, mexpect));
       
    }
    
    /**
     * Test of transform method, of class Mat3f.
     */
    @Test
    public void testAdjugate_transpose() {
      
      // non-invertible m
      Mat3f.set(m,
         1.0f, 2.0f, 3.0f,
         4.0f, 5.0f, 6.0f,
         7.0f, 8.0f, 9.0f    
      );
      float[] adj = new float[9];
      float det = Mat3f.adjugateTranspose(adj, m);
      Mat3f.set(mexpect,
        -3.0f,   6.0f, -3.0f,
         6.0f, -12.0f,  6.0f,
        -3.0f,   6.0f, -3.0f
      );
      //showM();
      //System.out.println("adj=\n" + Mat3f.toString(adj));
      //showMexpect();
      assertTrue(Mat3f.epsilonEquals(adj, mexpect, 0.001f));
      float detExpect = 0.0f;
      assertTrue(Math.abs(det - detExpect) < 0.0001);
//      float[] pr1 = new float[9];
//      float[] pr2 = new float[9];
//      Mat3f.mul(pr1, m, adj);
//      Mat3f.mul(pr2, adj, m);
      //System.out.println("pr1=\n" + Mat3f.toString(pr1));
      //System.out.println("pr2=\n" + Mat3f.toString(pr2));
      
   
      Mat3f.set(m,
         1.0f,  2.0f,  1.0f,
         3.0f,  2.0f,  1.0f,
         1.0f,  2.0f,  3.0f    
      );
      det = Mat3f.adjugateTranspose(adj, m);
      Mat3f.set(mexpect,
         4.0f,  -8.0f,  4.0f,
        -4.0f,   2.0f,  0.0f,
         0.0f,   2.0f, -4.0f
      );
      //showM();
      //System.out.println("adj=\n" + Mat3f.toString(adj));
      //showMexpect();
      assertTrue(Mat3f.epsilonEquals(adj, mexpect, 0.001f));
      detExpect = -8.0f;
      assertTrue(Math.abs(det - detExpect) < 0.0001);
   
    }
    
    
    /**
     * Test of transform method, of class Mat3f.
     */
    @Test
    public void testAdjugate_transposeMat4f() {
      float[] m4 = Mat4f.getMat4f();
      // non-invertible m4
      Mat4f.set(m4,
         1.0f, 2.0f, 3.0f, 66.6f,
         4.0f, 5.0f, 6.0f, 66.6f,
         7.0f, 8.0f, 9.0f,  66.6f,
         66.6f, 66.6f, 66.6f, 66.6f
      );
      float[] adj = new float[9];
      float det = Mat3f.adjugateTransposeMat4f(adj, m4);
      Mat3f.set(mexpect,
        -3.0f,   6.0f, -3.0f,
         6.0f, -12.0f,  6.0f,
        -3.0f,   6.0f, -3.0f
      );
      //showM();
      //System.out.println("adj=\n" + Mat3f.toString(adj));
      //showMexpect();
      assertTrue(Mat3f.epsilonEquals(adj, mexpect, 0.001f));
      float detExpect = 0.0f;
      assertTrue(Math.abs(det - detExpect) < 0.0001);

      
   
      Mat4f.set(m4,
         1.0f,  2.0f,  1.0f, 66.6f,
         3.0f,  2.0f,  1.0f, 66.6f,
         1.0f,  2.0f,  3.0f, 66.6f,
         66.6f, 66.6f, 66.6f, 66.6f
      );
      det = Mat3f.adjugateTransposeMat4f(adj, m4);
      Mat3f.set(mexpect,
         4.0f,  -8.0f,  4.0f,
        -4.0f,   2.0f,  0.0f,
         0.0f,   2.0f, -4.0f
      );
      //showM();
      //System.out.println("adj=\n" + Mat3f.toString(adj));
      //showMexpect();
      assertTrue(Mat3f.epsilonEquals(adj, mexpect, 0.001f));
      detExpect = -8.0f;
      assertTrue(Math.abs(det - detExpect) < 0.0001);
   
    }
    
    
    
    
    
    
    
    
    
    
    
    /**
     * Test matrix norms 1, 2, inf
     */
    @Test
    public void testNorm() {
      
   
      Mat3f.set(m,
         1.0f, 2.0f, 3.0f,
         4.0f, 5.0f, 1.0f,
         2.0f, 3.0f, 2.0f    
      );
      float n_1 = Mat3f.norm1(m);
      float n_2 = Mat3f.norm2(m);
      float n_inf = Mat3f.normInf(m);
      //System.out.println("m norms: " + n_1 + ", " + n_2 + ", " + n_inf);
      assertTrue(n_1 == 23.0f);
      
      assertTrue(n_inf == 5.0f);
      assertTrue(Math.abs(n_2 - 8.544f) < 0.001);
    
   }
    
    /**
     * Test of transform method, of class Mat3f.
     */
    @Test
    public void testAdjugate() {
      
      // non-invertible m
      Mat3f.set(m,
         1.0f, 2.0f, 3.0f,
         4.0f, 5.0f, 6.0f,
         7.0f, 8.0f, 9.0f    
      );
      float[] adj = new float[9];
      float det = Mat3f.adjugate(adj, m);
      Mat3f.set(mexpect,
        -3.0f,   6.0f, -3.0f,
         6.0f, -12.0f,  6.0f,
        -3.0f,   6.0f, -3.0f
      );
      //showM();
      //System.out.println("adj=\n" + Mat3f.toString(adj));
      //showMexpect();
      assertTrue(Mat3f.epsilonEquals(adj, mexpect, 0.001f));
      float detExpect = 0.0f;
      assertTrue(Math.abs(det - detExpect) < 0.001);
//      float[] pr1 = new float[9];
//      float[] pr2 = new float[9];
//      Mat3f.mul(pr1, m, adj);
//      Mat3f.mul(pr2, adj, m);
      //System.out.println("pr1=\n" + Mat3f.toString(pr1));
      //System.out.println("pr2=\n" + Mat3f.toString(pr2));
      
   
      Mat3f.set(m,
         1.0f,  2.0f,  1.0f,
         3.0f,  2.0f,  1.0f,
         1.0f,  2.0f,  3.0f    
      );
      det = Mat3f.adjugate(adj, m);
      Mat3f.set(mexpect,
         4.0f,  -4.0f,  0.0f,
        -8.0f,   2.0f,  2.0f,
         4.0f,   0.0f, -4.0f
      );
//      showM();
//      System.out.println("adj=\n" + Mat3f.toString(adj));
//      showMexpect();
//      System.out.println("det m = " + det);
      assertTrue(Mat3f.epsilonEquals(adj, mexpect, 0.001f));
      detExpect = -8.0f;
      assertTrue(Math.abs(det - detExpect) < 0.001);
   
    }
    
    
    /**
     * Test of transform method, of class Mat3f.
     */
    @Test
    @SuppressWarnings("deprecation")
    public void testPolarDecompose() {

      // First test a pure rotation matrix, no scaling. This should yield m back in Q, and S should be identity
      float ca = (float) Math.cos(0.6);
      float sa = (float) Math.sin(0.6);
      Mat3f.set(m,
         1.0f, 0.0f, 0.0f,
         0.0f, ca,   -sa,
         0.0f, sa,    ca    
      );
      float[] Q = new float[9];
      float[] S = new float[9];
      float[] Qexpect = new float[9];
      float[] Sexpect = new float[9];
      Mat3f.set(Qexpect,
         1.0f, 0.0f, 0.0f,
         0.0f, ca,   -sa,
         0.0f, sa,    ca    
      );
      Mat3f.set(Sexpect,
         1.0f, 0.0f, 0.0f,
         0.0f, 1.0f, 0.0f,
         0.0f, 0.0f, 1.0f    
      );
      float epsilon = 0.0001f; // determines smoothing for the scaling and the rotation
      Mat3f.polarDecompose(m, Q, S);
      Mat3f.ScalingType scaleType = Mat3f.getScalingType(S);
//      System.out.println("m=\n" + Mat3f.toString(m));
//      System.out.println("Q=\n" + Mat3f.toString(Q));
//      System.out.println("S=\n" + Mat3f.toString(S));
      assertTrue(Mat3f.epsilonEquals(Q, Qexpect, 0.001f));
      assertTrue(Mat3f.epsilonEquals(S, Sexpect, 0.001f));
      assertTrue(scaleType == Mat3f.ScalingType.IDENTITY);

      // Non-uniform, aligned scaling:
      float[] scales = new float[]{ 2.0f, 3.0f, 4.0f};
      float[] scale = Mat3f.getScalingMatrix(scales);
      Mat3f.mul(m, scale);
       
      Mat3f.set(Qexpect,
         1.0f, 0.0f, 0.0f,
         0.0f, ca,   -sa,
         0.0f, sa,    ca    
      );
      Mat3f.set(Sexpect,
         2.0f, 0.0f, 0.0f,
         0.0f, 3.0f, 0.0f,
         0.0f, 0.0f, 4.0f    
      );
      Mat3f.polarDecompose(m, Q, S);
      scaleType = Mat3f.getScalingType(S);
      assertTrue(Mat3f.epsilonEquals(Q, Qexpect, 0.001f));
      assertTrue(Mat3f.epsilonEquals(S, Sexpect, 0.001f));
      assertTrue(scaleType == Mat3f.ScalingType.ALIGNED);
      
      // Uniform,  scaling:
      Mat3f.set(m,
         1.0f, 0.0f, 0.0f,
         0.0f, ca,   -sa,
         0.0f, sa,    ca    
      );
      scales = new float[]{ 777.0f, 777.0f, 777.0f};
      scale = Mat3f.getScalingMatrix(scales);
      Mat3f.mul(m, scale);
       
      Mat3f.set(Qexpect,
         1.0f, 0.0f, 0.0f,
         0.0f, ca,   -sa,
         0.0f, sa,    ca    
      );
      Mat3f.set(Sexpect,
         777.0f, 0.0f, 0.0f,
         0.0f, 777.0f, 0.0f,
         0.0f, 0.0f, 777.0f    
      );
      Mat3f.polarDecompose(m, Q, S);
      scaleType = Mat3f.getScalingType(S);
//      System.out.println("m=\n" + Mat3f.toString(m));
//      System.out.println("Q=\n" + Mat3f.toString(Q));
//      System.out.println("S=\n" + Mat3f.toString(S));
      
      assertTrue(Mat3f.epsilonEquals(Q, Qexpect, 0.001f));
      assertTrue(Mat3f.epsilonEquals(S, Sexpect, 0.001f));
      assertTrue(scaleType == Mat3f.ScalingType.ALIGNED);

      // Non-uniform non-aligned scaling:
      Mat3f.set(m,
         1.0f, 0.0f, 0.0f,
         0.0f, ca,   -sa,
         0.0f, sa,    ca    
      );
     
      Mat3f.set(scale, 
         2.0f, 0.0f, 0.0f,
         0.0f, 4.0f, 0.0f,
         0.0f, 0.0f, 5.0f
      );
      float[] scaleRotation = new float[9];
      float[] inverseScaleRotation = new float[9];
      float[] axis = new float[]{1.0f, 1.0f, 1.0f};
      float angle = (float) Math.PI/5.0f;
      Mat3f.setFromAxisAngleScale(scaleRotation, axis, angle, 1.0f) ;
      Mat3f.invert(inverseScaleRotation, scaleRotation);
      Mat3f.mul(scale, scaleRotation, scale);
      Mat3f.mul(scale, scale, inverseScaleRotation);
      
      Mat3f.mul(m, scale);
       
      Mat3f.set(Qexpect,
         1.0f, 0.0f, 0.0f,
         0.0f, ca,   -sa,
         0.0f, sa,    ca    
      );
      
      scaleType = Mat3f.polarDecompose(m, Q, S, epsilon);
//      System.out.println("scale=\n" + Mat3f.toString(scale));
//      System.out.println("m=\n" + Mat3f.toString(m));
//      System.out.println("Q=\n" + Mat3f.toString(Q));
//      System.out.println("S=\n" + Mat3f.toString(S));
      
      assertTrue(Mat3f.epsilonEquals(Q, Qexpect, 0.001f));
      assertTrue(Mat3f.epsilonEquals(S, scale, 0.001f));
      assertTrue(scaleType == Mat3f.ScalingType.SKEW);

  
    }
    
    
    @Test
    public void testTransformTranspose()
    {
        float a[] = new float[3];
        float b[] = new float[3];
        float m[] = new float[9];
        float mT[] = new float[9];
        Vec3f.set(b,1,2,3);
        float[] aa = {0.55f, 0.55f, 0.55f, 3f};
        Mat3f.setFromAxisAngleScale(m, aa, 1.0f);
        Mat3f.transformTranspose(m,a,b);
        Mat3f.transpose(mT,m);
        Mat3f.transformVec3f(mT,b);
        //System.out.println("(a,b)"+Vec3f.toString(a)+Vec3f.toString(b));
        assertTrue(Vec3f.equals(a, b));
        
    }

   
    /**
     * Test of getIdentity method, of class Mat3f.
     */
    @Test
    public void getIdentity() {
        float[] result = Mat3f.getIdentity();
        assertTrue(Mat3f.isIdentity(result));        
    }

    /**
     * Test of skew method, of class Mat3f.
     */
    @Test
    public void skew()
    {
        //skew(a)*b == a x b
        
        float a[] = new float[3];
        float b[] = new float[3];
        float c[] = new float[3];
        float d[] = new float[3];
        float m[] = new float[9];
        for(int i=0;i<3;i++)
        {
            a[i]=i;
            b[i]=9-i;
        }
        Mat3f.skew(m, a);
        Mat3f.transform(m, c, b);
        Vec3f.cross(d, a, b);
        assertTrue(Vec3f.epsilonEquals(c, d, 0.0005f));
    }
    
    /**
     * Test of invert method, of class Mat3f.
     */
    @Test
    public void testInvert() {
        //m*m^-1=I
        float m[] = new float[9];
        float minv[] = new float[9];
        for(int i=0;i<9;i++)
        {
            m[i]=(float)Math.sqrt(i+1);
        }
        //System.out.println(Mat3f.det(m));
        
        float det = Mat3f.invert(minv, m);
        //System.out.println("det = " + det);
        //System.out.println("m^-1"+Mat3f.toString(minv));
        Mat3f.mul(m, minv);
        //System.out.println("m*m^-1"+Mat3f.toString(m));
        assertTrue(Mat3f.epsilonEquals(m, Mat3f.getIdentity(), 0.0005f));
        Mat3f.set(m,
         1.0f, 2.0f, 3.0f,
         4.0f, 5.0f, 6.0f,
         7.0f, 8.0f, 9.0f    
        );
        det = Mat3f.invert(minv, m);
        assertTrue(det == 0.0f); // so, non-invertibe
    }
    
    /**
     * Test of mulTransposeRight method, of class Mat3f.
     */
    @Test
    public void mulTransposeRight()
    {
        float m[] = new float[9];        
        float mT[] = new float[9];
        float dest[] = new float[9];
        float dest2[] = new float[9];
        for(int i=0;i<9;i++)
        {
            m[i]=i+1;
        }
        for(int i=0;i<9;i++)
        {
            mT[i]=-i-2;
        }
        Mat3f.mulTransposeRight(dest, m, mT);
        Mat3f.transpose(mT);
        Mat3f.mul(dest2, m, mT);
        assertTrue(Mat3f.epsilonEquals(dest, dest2, 0.0005f));
    }
    
    
    /**
     * Test of transpose method, of class Mat3f.
     */
    @Test
    public void transpose() {
        //System.out.println("transpose");
        
        float[] m1 = new float[9];
        for (int i=0;i<9;i++)
        {
            m1[i]=i;
        }
        float m2[]= new float[9];
        //((m1)T)T = m1
        Mat3f.transpose(m2,m1);
        Mat3f.transpose(m2);
        for(int i=0;i<9;i++)
        {
            assertTrue(m1[i]==m2[i]);
        }
    }
    
    /**
     * Test of isDiagonal method, of class Mat3f.
     */
    @Test
    public void testIsDiagonal() {
      
        float[] m= new float[9];
        Mat3f.set(m,
           1.0f, 0.0f, 0.0f,
           0.0f, 2.0f, 0.0f,
           0.0f, 0.0f, 3.0f
        );
        assertTrue(Mat3f.isDiagonal(m, 0.01f));
        
        Mat3f.set(m,
           1.0f, 1.0f, 0.0f,
           0.0f, 2.0f, 0.0f,
           0.0f, 0.0f, 3.0f
        );
        assertFalse(  Mat3f.isDiagonal(m, 0.01f));
        
        Mat3f.set(m,
           1.0f,       0.00001f,  0.000001f,
           0.000001f,  2.0f,      0.000001f,
           0.000001f,  0.000001f, 3.0f
        );
        assertTrue(Mat3f.isDiagonal(m, 0.001f));
        assertFalse(  Mat3f.isDiagonal(m, 0.0000001f));
        Mat3f.set(m,
           0.0f, 0.0f, 0.0f,
           0.0f, 0.0f, 0.0f,
           0.0f, 0.0f, 0.0f
        );
        
        assertTrue(  Mat3f.isDiagonal(m, 0.01f));
    }
    
    /**
     * Test of isSymmetric method, of class Mat3f.
     */
    @Test
    public void testIsSymmetric() {
      
        float[] m= new float[9];
        Mat3f.set(m,
           1.0f, 2.0f, 3.0f,
           2.0f, 4.0f, 5.0f,
           3.0f, 5.0f, 6.0f
        );
        assertTrue(Mat3f.isSymmetric(m, 0.0001f));
        Mat3f.set(m,
           1.0f, 6.0f, 3.0f,
           2.0f, 4.0f, 5.0f,
           3.0f, 5.0f, 6.0f
        );
        assertFalse( Mat3f.isSymmetric(m, 0.0001f));
        Mat3f.set(m,
           1.0f, 2.0f, 7.0f,
           2.0f, 4.0f, 5.0f,
           3.0f, 5.0f, 6.0f
        );
        assertFalse( Mat3f.isSymmetric(m, 0.0001f));
        Mat3f.set(m,
           1.0f, 6.0f, 3.0f,
           2.0f, 4.0f, 8.0f,
           3.0f, 5.0f, 6.0f
        );
        assertFalse( Mat3f.isSymmetric(m, 0.0001f));
        
        
        
    }
    
    @Test
    public void setRot()
    {
        float a = 0.7f;
        float q[] = new float[4];
        float m2[] = new float[9];
        float m1[] = new float[9];
        Mat3f.setXRot(m2,a);
        Quat4f.setFromAxisAngle4f(q, 1, 0, 0, a);
        Mat3f.setFromQuatScale(m1, q, 1f);
        assertTrue(Mat3f.epsilonEquals(m1, m2, 0.0000001f));
        
        a = 1f;
        Mat3f.setYRot(m2,a);
        Quat4f.setFromAxisAngle4f(q, 0, 1, 0, a);
        Mat3f.setFromQuatScale(m1, q, 1f);
        assertTrue(Mat3f.epsilonEquals(m1, m2, 0.0000001f));
        
        a = 1f;
        Mat3f.setZRot(m2,a);
        Quat4f.setFromAxisAngle4f(q, 0, 0, 1, a);
        Mat3f.setFromQuatScale(m1, q, 1f);
        assertTrue(Mat3f.epsilonEquals(m1, m2, 0.0000001f));
    }
    
}
