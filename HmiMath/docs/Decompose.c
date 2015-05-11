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
/**** Decompose.c ****/
/* Ken Shoemake, 1993 */
#include &lt;math.h&gt;
#include "Decompose.h"

/******* Matrix Preliminaries *******/

/** Fill out 3x3 matrix to 4x4 **/
#define mat_pad(A) =
(A[W][X]=3DA[X][W]=3DA[W][Y]=3DA[Y][W]=3DA[W][Z]=3DA[Z][W]=3D0,A[W][W]=3D=
1)

/** Copy nxn matrix A to C using "gets" for assignment **/
#define mat_copy(C,gets,A,n) {int i,j; for(i=3D0;i&lt;n;i++) =
for(j=3D0;j&lt;n;j++)\
    C[i][j] gets (A[i][j]);}

/** Copy transpose of nxn matrix A to C using "gets" for assignment **/
#define mat_tpose(AT,gets,A,n) {int i,j; for(i=3D0;i&lt;n;i++) =
for(j=3D0;j&lt;n;j++)\
    AT[i][j] gets (A[j][i]);}

/** Assign nxn matrix C the element-wise combination of A and B using =
"op" **/
#define mat_binop(C,gets,A,op,B,n) {int i,j; for(i=3D0;i&lt;n;i++) =
for(j=3D0;j&lt;n;j++)\
    C[i][j] gets (A[i][j]) op (B[i][j]);}

/** Multiply the upper left 3x3 parts of A and B to get AB **/
void mat_mult(HMatrix A, HMatrix B, HMatrix AB)
{
    int i, j;
    for (i=3D0; i&lt;3; i++) for (j=3D0; j&lt;3; j++)
   AB[i][j] =3D A[i][0]*B[0][j] + A[i][1]*B[1][j] + A[i][2]*B[2][j];
}

/** Return dot product of length 3 vectors va and vb **/
float vdot(float *va, float *vb)
{
    return (va[0]*vb[0] + va[1]*vb[1] + va[2]*vb[2]);
}

/** Set v to cross product of length 3 vectors va and vb **/
void vcross(float *va, float *vb, float *v)
{
    v[0] =3D va[1]*vb[2] - va[2]*vb[1];
    v[1] =3D va[2]*vb[0] - va[0]*vb[2];
    v[2] =3D va[0]*vb[1] - va[1]*vb[0];
}

/** Set MadjT to transpose of inverse of M times determinant of M **/
void adjoint_transpose(HMatrix M, HMatrix MadjT)
{
    vcross(M[1], M[2], MadjT[0]);
    vcross(M[2], M[0], MadjT[1]);
    vcross(M[0], M[1], MadjT[2]);
}

/******* Quaternion Preliminaries *******/

/* Construct a (possibly non-unit) quaternion from real components. */
Quat Qt_(float x, float y, float z, float w)
{
    Quat qq;
    qq.x =3D x; qq.y =3D y; qq.z =3D z; qq.w =3D w;
    return (qq);
}

/* Return conjugate of quaternion. */
Quat Qt_Conj(Quat q)
{
    Quat qq;
    qq.x =3D -q.x; qq.y =3D -q.y; qq.z =3D -q.z; qq.w =3D q.w;
    return (qq);
}

/* Return quaternion product qL * qR.  Note: order is important!
 * To combine rotations, use the product Mul(qSecond, qFirst),
 * which gives the effect of rotating by qFirst then qSecond. */
Quat Qt_Mul(Quat qL, Quat qR)
{
    Quat qq;
    qq.w =3D qL.w*qR.w - qL.x*qR.x - qL.y*qR.y - qL.z*qR.z;
    qq.x =3D qL.w*qR.x + qL.x*qR.w + qL.y*qR.z - qL.z*qR.y;
    qq.y =3D qL.w*qR.y + qL.y*qR.w + qL.z*qR.x - qL.x*qR.z;
    qq.z =3D qL.w*qR.z + qL.z*qR.w + qL.x*qR.y - qL.y*qR.x;
    return (qq);
}

/* Return product of quaternion q by scalar w. */
Quat Qt_Scale(Quat q, float w)
{
    Quat qq;
    qq.w =3D q.w*w; qq.x =3D q.x*w; qq.y =3D q.y*w; qq.z =3D q.z*w;
    return (qq);
}

/* Construct a unit quaternion from rotation matrix.  Assumes matrix is
 * used to multiply column vector on the left: vnew =3D mat vold.  Works
 * correctly for right-handed coordinate system and right-handed =
rotations.
 * Translation and perspective components ignored. */
Quat Qt_FromMatrix(HMatrix mat)
{
    /* This algorithm avoids near-zero divides by looking for a large =
component
     * - first w, then x, y, or z.  When the trace is greater than zero,
     * |w| is greater than 1/2, which is as small as a largest component =
can be.
     * Otherwise, the largest diagonal entry corresponds to the largest =
of |x|,
     * |y|, or |z|, one of which must be larger than |w|, and at least =
1/2. */
    Quat qu;
    register double tr, s;

    tr =3D mat[X][X] + mat[Y][Y]+ mat[Z][Z];
    if (tr &gt;=3D 0.0) {
       s =3D sqrt(tr + mat[W][W]);
       qu.w =3D s*0.5;
       s =3D 0.5 / s;
       qu.x =3D (mat[Z][Y] - mat[Y][Z]) * s;
       qu.y =3D (mat[X][Z] - mat[Z][X]) * s;
       qu.z =3D (mat[Y][X] - mat[X][Y]) * s;
   } else {
       int h =3D X;
       if (mat[Y][Y] &gt; mat[X][X]) h =3D Y;
       if (mat[Z][Z] &gt; mat[h][h]) h =3D Z;
       switch (h) {
#define caseMacro(i,j,k,I,J,K) \
       case I:\
      s =3D sqrt( (mat[I][I] - (mat[J][J]+mat[K][K])) + mat[W][W] );\
      qu.i =3D s*0.5;\
      s =3D 0.5 / s;\
      qu.j =3D (mat[I][J] + mat[J][I]) * s;\
      qu.k =3D (mat[K][I] + mat[I][K]) * s;\
      qu.w =3D (mat[K][J] - mat[J][K]) * s;\
      break
       caseMacro(x,y,z,X,Y,Z);
       caseMacro(y,z,x,Y,Z,X);
       caseMacro(z,x,y,Z,X,Y);
       }
   }
    if (mat[W][W] !=3D 1.0) qu =3D Qt_Scale(qu, 1/sqrt(mat[W][W]));
    return (qu);
}
/******* Decomp Auxiliaries *******/

static HMatrix mat_id =3D {{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};

/** Compute either the 1 or infinity norm of M, depending on tpose **/
float mat_norm(HMatrix M, int tpose)
{
    int i;
    float sum, max;
    max =3D 0.0;
    for (i=3D0; i&lt;3; i++) {
   if (tpose) sum =3D fabs(M[0][i])+fabs(M[1][i])+fabs(M[2][i]);
   else     sum =3D fabs(M[i][0])+fabs(M[i][1])+fabs(M[i][2]);
   if (max&lt;sum) max =3D sum;
    }
    return max;
}

float norm_inf(HMatrix M) {return mat_norm(M, 0);}
float norm_one(HMatrix M) {return mat_norm(M, 1);}

/** Return index of column of M containing maximum abs entry, or -1 if =
M=3D0 **/
int find_max_col(HMatrix M)
{
    float abs, max;
    int i, j, col;
    max =3D 0.0; col =3D -1;
    for (i=3D0; i&lt;3; i++) for (j=3D0; j&lt;3; j++) {
   abs =3D M[i][j]; if (abs&lt;0.0) abs =3D -abs;
   if (abs&gt;max) {max =3D abs; col =3D j;}
    }
    return col;
}

/** Setup u for Household reflection to zero all v components but first =
**/
void make_reflector(float *v, float *u)
{
    float s =3D sqrt(vdot(v, v));
    u[0] =3D v[0]; u[1] =3D v[1];
    u[2] =3D v[2] + ((v[2]&lt;0.0) ? -s : s);
    s =3D sqrt(2.0/vdot(u, u));
    u[0] =3D u[0]*s; u[1] =3D u[1]*s; u[2] =3D u[2]*s;
}

/** Apply Householder reflection represented by u to column vectors of M =
**/
void reflect_cols(HMatrix M, float *u)
{
    int i, j;
    for (i=3D0; i&lt;3; i++) {
   float s =3D u[0]*M[0][i] + u[1]*M[1][i] + u[2]*M[2][i];
   for (j=3D0; j&lt;3; j++) M[j][i] -=3D u[j]*s;
    }
}
/** Apply Householder reflection represented by u to row vectors of M =
**/
void reflect_rows(HMatrix M, float *u)
{
    int i, j;
    for (i=3D0; i&lt;3; i++) {
   float s =3D vdot(u, M[i]);
   for (j=3D0; j&lt;3; j++) M[i][j] -=3D u[j]*s;
    }
}

/** Find orthogonal factor Q of rank 1 (or less) M **/
void do_rank1(HMatrix M, HMatrix Q)
{
    float v1[3], v2[3], s;
    int col;
    mat_copy(Q,=3D,mat_id,4);
    /* If rank(M) is 1, we should find a non-zero column in M */
    col =3D find_max_col(M);
    if (col&lt;0) return; /* Rank is 0 */
    v1[0] =3D M[0][col]; v1[1] =3D M[1][col]; v1[2] =3D M[2][col];
    make_reflector(v1, v1); reflect_cols(M, v1);
    v2[0] =3D M[2][0]; v2[1] =3D M[2][1]; v2[2] =3D M[2][2];
    make_reflector(v2, v2); reflect_rows(M, v2);
    s =3D M[2][2];
    if (s&lt;0.0) Q[2][2] =3D -1.0;
    reflect_cols(Q, v1); reflect_rows(Q, v2);
}

/** Find orthogonal factor Q of rank 2 (or less) M using adjoint =
transpose **/
void do_rank2(HMatrix M, HMatrix MadjT, HMatrix Q)
{
    float v1[3], v2[3];
    float w, x, y, z, c, s, d;
    int col;
    /* If rank(M) is 2, we should find a non-zero column in MadjT */
    col =3D find_max_col(MadjT);
    if (col&lt;0) {do_rank1(M, Q); return;} /* Rank&lt;2 */
    v1[0] =3D MadjT[0][col]; v1[1] =3D MadjT[1][col]; v1[2] =3D =
MadjT[2][col];
    make_reflector(v1, v1); reflect_cols(M, v1);
    vcross(M[0], M[1], v2);
    make_reflector(v2, v2); reflect_rows(M, v2);
    w =3D M[0][0]; x =3D M[0][1]; y =3D M[1][0]; z =3D M[1][1];
    if (w*z&gt;x*y) {
   c =3D z+w; s =3D y-x; d =3D sqrt(c*c+s*s); c =3D c/d; s =3D s/d;
   Q[0][0] =3D Q[1][1] =3D c; Q[0][1] =3D -(Q[1][0] =3D s);
    } else {
   c =3D z-w; s =3D y+x; d =3D sqrt(c*c+s*s); c =3D c/d; s =3D s/d;
   Q[0][0] =3D -(Q[1][1] =3D c); Q[0][1] =3D Q[1][0] =3D s;
    }
    Q[0][2] =3D Q[2][0] =3D Q[1][2] =3D Q[2][1] =3D 0.0; Q[2][2] =3D =
1.0;
    reflect_cols(Q, v1); reflect_rows(Q, v2);
}


/******* Polar Decomposition *******/

/* Polar Decomposition of 3x3 matrix in 4x4,
 * M =3D QS.  See Nicholas Higham and Robert S. Schreiber,
 * Fast Polar Decomposition of An Arbitrary Matrix,
 * Technical Report 88-942, October 1988,
 * Department of Computer Science, Cornell University.
 */
float polar_decomp(HMatrix M, HMatrix Q, HMatrix S)
{
#define TOL 1.0e-6
    HMatrix Mk, MadjTk, Ek;
    float det, M_one, M_inf, MadjT_one, MadjT_inf, E_one, gamma, g1, g2;
    int i, j;
    mat_tpose(Mk,=3D,M,3);
    M_one =3D norm_one(Mk);  M_inf =3D norm_inf(Mk);
    do {
       adjoint_transpose(Mk, MadjTk);
       det =3D vdot(Mk[0], MadjTk[0]);
       if (det=3D=3D0.0) {do_rank2(Mk, MadjTk, Mk); break;}
       MadjT_one =3D norm_one(MadjTk); MadjT_inf =3D norm_inf(MadjTk);
       gamma =3D sqrt(sqrt((MadjT_one*MadjT_inf)/(M_one*M_inf))/fabs(det));
       g1 =3D gamma*0.5;
       g2 =3D 0.5/(gamma*det);
       mat_copy(Ek,=3D,Mk,3);
       mat_binop(Mk,=3D,g1*Mk,+,g2*MadjTk,3);
       mat_copy(Ek,-=3D,Mk,3);
       E_one =3D norm_one(Ek);
       M_one =3D norm_one(Mk);  M_inf =3D norm_inf(Mk);
    } while (E_one&gt;(M_one*TOL));
    mat_tpose(Q,=3D,Mk,3); mat_pad(Q);
    mat_mult(Mk, M, S);  mat_pad(S);
    for (i=3D0; i&lt;3; i++) for (j=3Di; j&lt;3; j++)
   S[i][j] =3D S[j][i] =3D 0.5*(S[i][j]+S[j][i]);
    return (det);
}

















/******* Spectral Decomposition *******/

/* Compute the spectral decomposition of symmetric positive =
semi-definite S.
 * Returns rotation in U and scale factors in result, so that if K is a =
diagonal
 * matrix of the scale factors, then S =3D U K (U transpose). Uses =
Jacobi method.
 * See Gene H. Golub and Charles F. Van Loan. Matrix Computations. =
Hopkins 1983.
 */
HVect spect_decomp(HMatrix S, HMatrix U)
{
    HVect kv;
    double Diag[3],OffD[3]; /* OffD is off-diag (by omitted index) */
    double g,h,fabsh,fabsOffDi,t,theta,c,s,tau,ta,OffDq,a,b;
    static char nxt[] =3D {Y,Z,X};
    int sweep, i, j;
    mat_copy(U,=3D,mat_id,4);
    Diag[X] =3D S[X][X]; Diag[Y] =3D S[Y][Y]; Diag[Z] =3D S[Z][Z];
    OffD[X] =3D S[Y][Z]; OffD[Y] =3D S[Z][X]; OffD[Z] =3D S[X][Y];
    for (sweep=3D20; sweep&gt;0; sweep--) {
   float sm =3D fabs(OffD[X])+fabs(OffD[Y])+fabs(OffD[Z]);
   if (sm=3D=3D0.0) break;
   for (i=3DZ; i&gt;=3DX; i--) {
       int p =3D nxt[i]; int q =3D nxt[p];
       fabsOffDi =3D fabs(OffD[i]);
       g =3D 100.0*fabsOffDi;
       if (fabsOffDi&gt;0.0) {
      h =3D Diag[q] - Diag[p];
      fabsh =3D fabs(h);
      if (fabsh+g=3D=3Dfabsh) {
          t =3D OffD[i]/h;
      } else {
          theta =3D 0.5*h/OffD[i];
          t =3D 1.0/(fabs(theta)+sqrt(theta*theta+1.0));
          if (theta&lt;0.0) t =3D -t;
      }
      c =3D 1.0/sqrt(t*t+1.0); s =3D t*c;
      tau =3D s/(c+1.0);
      ta =3D t*OffD[i]; OffD[i] =3D 0.0;
      Diag[p] -=3D ta; Diag[q] +=3D ta;
      OffDq =3D OffD[q];
      OffD[q] -=3D s*(OffD[p] + tau*OffD[q]);
      OffD[p] +=3D s*(OffDq   - tau*OffD[p]);
      for (j=3DZ; j&gt;=3DX; j--) {
          a =3D U[j][p]; b =3D U[j][q];
          U[j][p] -=3D s*(b + tau*a);
          U[j][q] +=3D s*(a - tau*b);
      }
       }
   }
    }
    kv.x =3D Diag[X]; kv.y =3D Diag[Y]; kv.z =3D Diag[Z]; kv.w =3D 1.0;
    return (kv);
}

/******* Spectral Axis Adjustment *******/

/* Given a unit quaternion, q, and a scale vector, k, find a unit =
quaternion, p,
 * which permutes the axes and turns freely in the plane of duplicate =
scale
 * factors, such that q p has the largest possible w component, i.e. the
 * smallest possible angle. Permutes k's components to go with q p =
instead of q.
 * See Ken Shoemake and Tom Duff. Matrix Animation and Polar =
Decomposition.
 * Proceedings of Graphics Interface 1992. Details on p. 262-263.
 */
Quat snuggle(Quat q, HVect *k)
{
#define SQRTHALF (0.7071067811865475244)
#define sgn(n,v)    ((n)?-(v):(v))
#define swap(a,i,j) {a[3]=3Da[i]; a[i]=3Da[j]; a[j]=3Da[3];}
#define cycle(a,p)  if (p) {a[3]=3Da[0]; a[0]=3Da[1]; a[1]=3Da[2]; =
a[2]=3Da[3];}\
          else   {a[3]=3Da[2]; a[2]=3Da[1]; a[1]=3Da[0]; a[0]=3Da[3];}
    Quat p;
    float ka[4];
    int i, turn =3D -1;
    ka[X] =3D k-&gt;x; ka[Y] =3D k-&gt;y; ka[Z] =3D k-&gt;z;
    if (ka[X]=3D=3Dka[Y]) {if (ka[X]=3D=3Dka[Z]) turn =3D W; else turn =
=3D Z;}
    else {if (ka[X]=3D=3Dka[Z]) turn =3D Y; else if (ka[Y]=3D=3Dka[Z]) =
turn =3D X;}
    if (turn&gt;=3D0) {
   Quat qtoz, qp;
   unsigned neg[3], win;
   double mag[3], t;
   static Quat qxtoz =3D {0,SQRTHALF,0,SQRTHALF};
   static Quat qytoz =3D {SQRTHALF,0,0,SQRTHALF};
   static Quat qppmm =3D { 0.5, 0.5,-0.5,-0.5};
   static Quat qpppp =3D { 0.5, 0.5, 0.5, 0.5};
   static Quat qmpmm =3D {-0.5, 0.5,-0.5,-0.5};
   static Quat qpppm =3D { 0.5, 0.5, 0.5,-0.5};
   static Quat q0001 =3D { 0.0, 0.0, 0.0, 1.0};
   static Quat q1000 =3D { 1.0, 0.0, 0.0, 0.0};
   switch (turn) {
   default: return (Qt_Conj(q));
   case X: q =3D Qt_Mul(q, qtoz =3D qxtoz); swap(ka,X,Z) break;
   case Y: q =3D Qt_Mul(q, qtoz =3D qytoz); swap(ka,Y,Z) break;
   case Z: qtoz =3D q0001; break;
   }
   q =3D Qt_Conj(q);
   mag[0] =3D (double)q.z*q.z+(double)q.w*q.w-0.5;
   mag[1] =3D (double)q.x*q.z-(double)q.y*q.w;
   mag[2] =3D (double)q.y*q.z+(double)q.x*q.w;
   for (i=3D0; i&lt;3; i++) if (neg[i] =3D (mag[i]&lt;0.0)) mag[i] =3D =
-mag[i];
   if (mag[0]&gt;mag[1]) {if (mag[0]&gt;mag[2]) win =3D 0; else win =3D =
2;}
   else        {if (mag[1]&gt;mag[2]) win =3D 1; else win =3D 2;}
   switch (win) {
   case 0: if (neg[0]) p =3D q1000; else p =3D q0001; break;
   case 1: if (neg[1]) p =3D qppmm; else p =3D qpppp; cycle(ka,0) break;
   case 2: if (neg[2]) p =3D qmpmm; else p =3D qpppm; cycle(ka,1) break;
   }
   qp =3D Qt_Mul(q, p);
   t =3D sqrt(mag[win]+0.5);
   p =3D Qt_Mul(p, Qt_(0.0,0.0,-qp.z/t,qp.w/t));
   p =3D Qt_Mul(qtoz, Qt_Conj(p));
    } else {
   float qa[4], pa[4];
   unsigned lo, hi, neg[4], par =3D 0;
   double all, big, two;
   qa[0] =3D q.x; qa[1] =3D q.y; qa[2] =3D q.z; qa[3] =3D q.w;
   for (i=3D0; i&lt;4; i++) {
       pa[i] =3D 0.0;
       if (neg[i] =3D (qa[i]&lt;0.0)) qa[i] =3D -qa[i];
       par ^=3D neg[i];
   }
   /* Find two largest components, indices in hi and lo */
   if (qa[0]&gt;qa[1]) lo =3D 0; else lo =3D 1;
   if (qa[2]&gt;qa[3]) hi =3D 2; else hi =3D 3;
   if (qa[lo]&gt;qa[hi]) {
       if (qa[lo^1]&gt;qa[hi]) {hi =3D lo; lo ^=3D 1;}
       else {hi ^=3D lo; lo ^=3D hi; hi ^=3D lo;}
   } else {if (qa[hi^1]&gt;qa[lo]) lo =3D hi^1;}
   all =3D (qa[0]+qa[1]+qa[2]+qa[3])*0.5;
   two =3D (qa[hi]+qa[lo])*SQRTHALF;
   big =3D qa[hi];
   if (all&gt;two) {
       if (all&gt;big) {/*all*/
      {int i; for (i=3D0; i&lt;4; i++) pa[i] =3D sgn(neg[i], 0.5);}
      cycle(ka,par)
       } else {/*big*/ pa[hi] =3D sgn(neg[hi],1.0);}
   } else {
       if (two&gt;big) {/*two*/
      pa[hi] =3D sgn(neg[hi],SQRTHALF); pa[lo] =3D sgn(neg[lo], SQRTHALF);
      if (lo&gt;hi) {hi ^=3D lo; lo ^=3D hi; hi ^=3D lo;}
      if (hi=3D=3DW) {hi =3D "\001\002\000"[lo]; lo =3D 3-hi-lo;}
      swap(ka,hi,lo)
       } else {/*big*/ pa[hi] =3D sgn(neg[hi],1.0);}
   }
   p.x =3D -pa[0]; p.y =3D -pa[1]; p.z =3D -pa[2]; p.w =3D pa[3];
    }
    k-&gt;x =3D ka[X]; k-&gt;y =3D ka[Y]; k-&gt;z =3D ka[Z];
    return (p);
}











/******* Decompose Affine Matrix *******/

/* Decompose 4x4 affine matrix A as TFRUK(U transpose), where t contains =
the
 * translation components, q contains the rotation R, u contains U, k =
contains
 * scale factors, and f contains the sign of the determinant.
 * Assumes A transforms column vectors in right-handed coordinates.
 * See Ken Shoemake and Tom Duff. Matrix Animation and Polar =
Decomposition.
 * Proceedings of Graphics Interface 1992.
 */
void decomp_affine(HMatrix A, AffineParts *parts)
{
    HMatrix Q, S, U;
    Quat p;
    float det;
    parts-&gt;t =3D Qt_(A[X][W], A[Y][W], A[Z][W], 0);
    det =3D polar_decomp(A, Q, S);
    if (det&lt;0.0) {
   mat_copy(Q,=3D,-Q,3);
   parts-&gt;f =3D -1;
    } else parts-&gt;f =3D 1;
    parts-&gt;q =3D Qt_FromMatrix(Q);
    parts-&gt;k =3D spect_decomp(S, U);
    parts-&gt;u =3D Qt_FromMatrix(U);
    p =3D snuggle(parts-&gt;u, &amp;parts-&gt;k);
    parts-&gt;u =3D Qt_Mul(parts-&gt;u, p);
}

/******* Invert Affine Decomposition *******/

/* Compute inverse of affine decomposition.
 */
void invert_affine(AffineParts *parts, AffineParts *inverse)
{
    Quat t, p;
    inverse-&gt;f =3D parts-&gt;f;
    inverse-&gt;q =3D Qt_Conj(parts-&gt;q);
    inverse-&gt;u =3D Qt_Mul(parts-&gt;q, parts-&gt;u);
    inverse-&gt;k.x =3D (parts-&gt;k.x=3D=3D0.0) ? 0.0 : =
1.0/parts-&gt;k.x;
    inverse-&gt;k.y =3D (parts-&gt;k.y=3D=3D0.0) ? 0.0 : =
1.0/parts-&gt;k.y;
    inverse-&gt;k.z =3D (parts-&gt;k.z=3D=3D0.0) ? 0.0 : =
1.0/parts-&gt;k.z;
    inverse-&gt;k.w =3D parts-&gt;k.w;
    t =3D Qt_(-parts-&gt;t.x, -parts-&gt;t.y, -parts-&gt;t.z, 0);
    t =3D Qt_Mul(Qt_Conj(inverse-&gt;u), Qt_Mul(t, inverse-&gt;u));
    t =3D Qt_(inverse-&gt;k.x*t.x, inverse-&gt;k.y*t.y, =
inverse-&gt;k.z*t.z, 0);
    p =3D Qt_Mul(inverse-&gt;q, inverse-&gt;u);
    t =3D Qt_Mul(p, Qt_Mul(t, Qt_Conj(p)));
    inverse-&gt;t =3D (inverse-&gt;f&gt;0.0) ? t : Qt_(-t.x, -t.y, -t.z, =
0);
}

