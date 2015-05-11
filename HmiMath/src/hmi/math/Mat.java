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
package hmi.math;

/**
 * Generic Matrix algorithmns
 * @author Herwin van Welbergen, Job Zwiers
 */
public final class Mat {
   
   private Mat(){} 
   /**
    * Calculates a LU decomposition of matrix m,
    * and stores the result back in m.
    * The int[] p receives the permutations, due to
    * partial pivoting. The boolean result denotes
    * whether the matrix is non-singular.
    */
   public static boolean LUDecompose(float[] matrix, int m, int n, int[] p) {
      // p is the current row permutation.
      int psign = +1; // sign of the permutation array p.
      float[] s = new float[m]; // row-scales for pivoting
      for (int i=0; i<m; i++) {
         p[i] = i;
         s[i] = 0.0f;
         for (int j=0; j<n; j++) {
            s[i] = Math.max(s[i], Math.abs(matrix[i*n + j]));
         }
      }
      
      for (int k = 0; k< m; k++) { // gaussian elimination for column k
         // determine pivot element
         int pivotIndex = k;
         float pivotMax = 0.0f;
         for (int i=k; i<m; i++) {
            int ri = p[i];
            float piv = Math.abs(matrix[ri*n + k])/s[ri];
            if ( piv > pivotMax) {
               pivotIndex = i; 
               pivotMax = piv;
            }
         }
         int pr = p[pivotIndex]; // pivot row
         if (pivotIndex != k) { // exchange rows if necessary
            p[pivotIndex] = p[k]; p[k] = pr; 
            psign = -psign; // keep track of the sign of the permutation.
         }
         float pivot = matrix[pr*n + k];
         if (pivot == 0.0f) return false;
         for (int i=k+1; i<m; i++) {
            float z = matrix[p[i]*n + k] / pivot;
            matrix[p[i]*n + k] = z;
            for (int j=k+1; j< n; j++) { // update row
               matrix[p[i]*n + j] -= z * matrix[pr*n + j];           
            }
         }
      }   
      return true;
   }
   
   
   /**
    * Solves the matrix equation A x = b, assuming that LU is an LU decomposition of P*A ,
    * where P is the permutation matrix corresponding to p, and obtained during 
    * partial pivoting for the LU decomposition.
    * with L containing 1 at the main diagonal (Doolittle's factorization)
    */
   public static float[] solveLUSystem(float[] LU, int[] p, float[] b) {
      int n = b.length;
      float[] x = new float[n];
      // first forward substitution, to solve L z = P b
      for (int i=0; i<n; i++) {
         int ri = p[i];
         for (int j=0; j<i; j++) {
            b[ri] -= LU[ri*n + j] * b[p[j]];  
         }
      } 
      // then backsubstitution to solve U x = P z
      for (int i=n-1; i>=0; i--) {
         int ri = p[i];
         x[i] = b[ri];
         for (int j=i+1; j<n; j++) {
            x[i] -= LU[ri*n + j] * x[j];  
         }
         x[i] /= LU[ri*n + i];
         
      }
      return x;
   }
   
   
   public static void invertMatrix(float[] A, int n, float[] Ainv) {
      float[] LU = new float[n*n];
      set(LU, A, n, n) ;
      int[] p = new int[n];
      LUDecompose(LU, n, n, p);
      float[] b = new float[n];
      for (int j=0; j<n; j++) {
         for (int i=0; i<n; i++) b[i] = 0.0f;
         b[j] = 1.0f;  
         float[] u = solveLUSystem(LU, p, b);
         for (int i=0; i<n; i++) Ainv[i*n + j] = u[i];
      }
        
   }
   
   
   /**
    * Copies an m X n array from src to dest
    */
   public static void set(float[] dest, float[] src, int m, int n) {
      for (int i=0; i<m*n; i++)  dest[i] = src[i];
   }
   
   /**
    * Extracts the L and U factors from m X m matrix "matrix", using the permutation p.
    * 
    */
   public static void getLUfactors(float[] matrix, int m, int[] p, float[] L, float[] U) {
       for (int i=0; i<m; i++) {
          int ri = p[i]*m;
          for (int j=0; j<m; j++) {
             if (j < i) {
                L[i*m + j] = matrix[ri + j];
                U[i*m + j] = 0.0f;
             } else if (j==i) {
                L[i*m + j] = 1.0f;
                U[i*m + j] = matrix[ri + j];
             } else {
                L[i*m + j] = 0.0f;
                U[i*m + j] = matrix[ri + j];
             }  
          }
       } 
   }
   
   
   /**
    * Inverts a permutation
    */
   public static int[] invertPermutation(int[] p) {
      int m = p.length;
      int[] invp = new int[m];
      for (int i=0; i<m; i++) {
         invp[p[i]] = i; 
      }
      return invp;
   }
   
   
   /**
    * Assuming that p is a length m array containg a 
    * permutation of 0..m-1, this method returns a
    * corresponding m X m permutation matrix
    * for permuting matrix rows.
    */
   public static float[] getPermutationMatrix(int[] p) {
      int m = p.length;
      float[] pmatrix = new float[m*m];
      //for (int i=0; i<m*m; i++) pmatrix[i] = 0.0f;
      for (int i=0; i<m; i++) {
          pmatrix[i*m + p[i]] = 1.0f;  
      } 
      return pmatrix;    
   }
   
   /**
    * Assuming that p is a length m array containg a 
    * permutation of 0..m-1, this method returns a
    * corresponding m X m inverse permutation matrix
    * for permuting matrix rows.
    */
   public static float[] getInvertedPermutationMatrix(int[] p) {
      int m = p.length;
      float[] invpmatrix = new float[m*m];
      for (int i=0; i<m; i++) {
          invpmatrix[p[i]*m + i] = 1.0f;  
      } 
      return invpmatrix;    
   }
   
   /**
    * permutes vector b, according to permutation p
    */
   public static float[] permute(int[] p, float[] b) {
       int m = p.length;
       float[] bp = new float[m];
       for (int i=0; i<m; i++) {
          bp[i] = b[p[i]];  
      } 
      return bp;  
   }
   
   
   
   
   /**
    * Solves Ax=d, with A the tridiagonal matrix
    * b1 c1 ..       0
    * a2 b2 c2 ..    0
    * 0  a3 b3 c3 .. 0
    *        ..
    * 0  ..       an bn
    * 
    * or alternatively written:     
    * ai*xi-1 + bi*xi + ci*xi+1 = di, a1=0, c1=0
    * 
    * Puts new values in c and d
    * 
    * Implementation from http://en.wikipedia.org/wiki/Tridiagonal_matrix_algorithm
    */
   public static void tridiagonalSolve(float []a, float []b, float []c, float []d, float []x)
   {
        int i;
        int n = a.length;
        
        //Modify the coefficients.
        c[0] = c[0]/b[0];                               //Division by zero risk.
        d[0] = d[0]/b[0];
        float id;
        for(i = 1; i < n; i++)
        {
                id = 1.0f/(b[i] - c[i - 1]*a[i]);        //Division by zero risk.
                c[i] = c[i]*id;                         //Last value calculated is redundant.
                d[i] = (d[i] - a[i]*d[i - 1])*id;
        }

        //Now back substitute.
        x[n - 1] = d[n - 1];
        for(i = n - 2; i != -1; i--)
        {
         x[i] = d[i] - c[i]*x[i + 1];
        }
   }
   
   /**
    * dst = A * B.<br>
    * dst cannot be aliased with A or B.<br>
    * A: an x A.length/an matrix.<br>
    * B: bn x B.length/bn matrix.<br>
    * 
    * Encoded in the same way as matrices used in Mat3f, Mat4f:<br>
    * A_ij = A[i*an+j] 
    */
   public static void mul(float[] dst, float[] a, int an, float[] b, int bn)
   {
       int am = a.length/an;
       int bm = b.length/bn;
       assert(dst.length>=am*bn);
       assert(an==bm);
       for(int i=0;i<am;i++)
       {
           for(int j=0;j<bn;j++)
           {
               dst[i*bn+j]=0;
               for(int k=0;k<an;k++)
               {
                   dst[i*bn+j]+=a[i*an+k]*b[k*bn+j];
               }
           }
       }
   }
}
