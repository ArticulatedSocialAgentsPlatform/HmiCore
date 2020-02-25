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
public class MatTest {

    public MatTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test for the LUDecompose method
     */
    /* doesn't actually test anything...
    @Test
    public void testLUDecompose() {
        float[] A = new float[9];
        int[] p = new int[3];
        
        Mat3f.set(A, 
           2.0f,  3.0f, -6.0f,
           1.0f, -6.0f,  8.0f,
           3.0f, -2.0f,  1.0f );
           
        float[] LU = new float[9];   
        //System.out.println("A=\n" + Mat3f.toString(A)); 
        Mat3f.set(LU, A);
        boolean notSingular = Mat.LUDecompose(LU, 3, 3, p);
        //System.out.println("LU=\n" + Mat3f.toString(LU)); 
        if (!notSingular) System.out.println("SINGULAR");
        //System.out.println("p=[" + p[0] + ", " + p[1] + ", " + p[2] + "]");
        
//        float[] L= new float[9];
//        float[] R= new float[9];
//        Mat.getLUfactors(LU, 3, p, L, R);
//        System.out.println("L=\n" + Mat3f.toString(L)); 
//        System.out.println("R=\n" + Mat3f.toString(R)); 
//        Mat3f.mul(L, R);
//        System.out.println("L*R=\n" + Mat3f.toString(L)); 
//        float[] P = Mat.getPermutationMatrix(p);
//        System.out.println("P=\n" + Mat3f.toString(P)); 
//        Mat3f.mul(P, A);
//        System.out.println("P*A=\n" + Mat3f.toString(P)); 
        
        float[] b1 = new float[] {2.0f, 1.0f, 3.0f};
        float[] x1 = Mat.solveLUSystem(LU, p, b1);
        //System.out.println("x1=" + Vec3f.toString(x1));
        float[] b2 = new float[] {3.0f, -6.0f, -2.0f};
        float[] x2 = Mat.solveLUSystem(LU, p, b2);
        //System.out.println("x2=" + Vec3f.toString(x2));
        
        float[] b3 = new float[] {8.0f, -11.0f, -1.0f};
        float[] x3 = Mat.solveLUSystem(LU, p, b3);
        //System.out.println("x3=" + Vec3f.toString(x3));
        
        float[] b4 = new float[] {-11.0f, 16.0f, 4.0f};
        float[] x4 = Mat.solveLUSystem(LU, p, b4);
        //System.out.println("x4=" + Vec3f.toString(x4));
        
    }
    */
    
    /**
     * Test matrix inversion
     */
    @Test
    public void testInvertMatrix() {
       float[] A = new float[9];
       float[] Ainv = new float[9];
   
        Mat3f.set(A, 
           2.0f,  3.0f, -6.0f,
           1.0f, -6.0f,  8.0f,
           3.0f, -2.0f,  1.0f );
       Mat.invertMatrix(A, 3, Ainv);
       //System.out.println("Ainv=\n" + Mat3f.toString(Ainv)); 
       float[] Prod = new float[9];
       Mat3f.mul(Prod, A, Ainv);
       //System.out.println("Prod=\n" + Mat3f.toString(Prod)); 
       
    }
    
    @Test
    public void testMul()
    {
        float m1[] = Mat4f.getMat4f();
        for(int i=0;i<16;i++)
        {
            m1[i]=i+2;
        }
        float m2[] = Mat4f.getMat4f();
        for(int i=0;i<16;i++)
        {
            m2[i]=i+4;
        }
        float m3[] = Mat4f.getMat4f();
        float m4[] = Mat4f.getMat4f();
        Mat4f.mul(m3, m1,m2);
        Mat.mul(m4, m1,4,m2,4);        
        assertTrue(Mat4f.epsilonEquals(m4, m3, 0.000001f));
        
        float v1[] = new float[3];
        float v2[] = new float[3];
        float v3[] = new float[1];
        Vec3f.set(v1, 1,2,3);
        Vec3f.set(v2, 3,4,5);
        Mat.mul(v3, v1, 3, v2, 1);
        assertTrue(Math.abs(Vec3f.dot(v1, v2)-v3[0])<0.000001f);
    }
//    @Test
//    public void testPermute() {
//       float[] b = new float[3];
//       Vec3f.set(b, 3.0f, 4.0f, 5.0f);  
//       int[] p = new int[] {2, 0, 1};
//       float[] bp = Mat.permute(p, b);
//       System.out.println("bp=" + Vec3f.toString(bp));
//       float[] P = Mat.getPermutationMatrix(p);
//       float[] bpp = new float[3];
//       Mat3f.transform(P, bpp, b);
//       System.out.println("bpp=" + Vec3f.toString(bpp));
//       
//    }
//
//
//    @Test
//    public void testPermute2() {
//       
//       int[] p = new int[] {2, 0, 1};
//       float[] pMatrix = Mat.getPermutationMatrix(p);
//       System.out.println("pMatrix=\n" + Mat3f.toString(pMatrix));
//       
//       
//       int[] pinv = Mat.invertPermutation(p);
//       float[] pinvMatrix = Mat.getPermutationMatrix(pinv);
//       System.out.println("pinvMatrix=\n" + Mat3f.toString(pinvMatrix));
//       
//       float[] InvPMatrix = Mat.getInvertedPermutationMatrix(p);
//       System.out.println("InvPMatrix=\n" + Mat3f.toString(InvPMatrix));
//       
//       float[] mp1  = new float[9];
//       Mat3f.mul(mp1, pMatrix, pinvMatrix);
//       System.out.println("pMatrix * pinvMatrix=\n" + Mat3f.toString(mp1));
//       
//       float[] mp2  = new float[9];
//       Mat3f.mul(mp2, pMatrix, InvPMatrix);
//       System.out.println("pMatrix * InvPMatrix=\n" + Mat3f.toString(mp2));
//       
//       
//       
//    }
}
