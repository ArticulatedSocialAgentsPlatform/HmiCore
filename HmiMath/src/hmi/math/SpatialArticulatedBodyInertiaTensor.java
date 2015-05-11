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
 * Efficient implementation of the 6x6 spatial articulated body inertia tensor 
 * As defined in
 * 
 * Rigid Body Dynamics Algorithms
 * Roy Featherstone
 * 2007
 * 
 * SpatialArticulatedBodyInertiaTensor are stored using a 27-element float array
 * 
 * Could be even more storage efficient if we use lower triangles of I and M
 * @author Herwin van Welbergen
 */
public final class SpatialArticulatedBodyInertiaTensor 
{
    public static final int I = 0;
    public static final int H = 9;
    public static final int M = 18;
    
    private SpatialArticulatedBodyInertiaTensor(){}
    
    /**
     * Returns a new float[27] array with zero components
     */
    public static float[] getSpatialArticulatedBodyInertiaTensor() 
    {
       return new float[27];
    }
    
    /**
     * Idst = I1 + I2 
     */
    public void add(float[] destTensor, float[] tensor1, float[] tensor2)
    {
      Mat3f.add(destTensor, tensor1, tensor2);
      Mat3f.add(destTensor,H,tensor1,H,tensor2,H);
      Mat3f.add(destTensor,M,tensor1,M,tensor2,M);
    }
    
    /**
     * Idst = I1 + I2 
     */
    public void add(float []destTensor, int dstIndex, float[] tensor1, int index1, float[] tensor2, int index2)
    {
      Mat3f.add(destTensor, dstIndex,tensor1,index1,tensor2,index2);
      Mat3f.add(destTensor,H+dstIndex,tensor1,H+index2,tensor2,H+index2);
      Mat3f.add(destTensor,M+dstIndex,tensor1,M+index1,tensor2,M+index2);
    }
    
    /**
     * Idst = I1 + I2, I2 is spatial inertia tensor (13 elements)
     */
    public void addSpatialInertia(float[] destTensor, float[] tensor1, float[] tensor2)
    {
      //M = M1 + m2 1 
      Mat3f.setIdentity(destTensor,M);
      Mat3f.scale(destTensor, M,tensor2[SpatialInertiaTensor.M]);
      Mat3f.add(destTensor,M,tensor1,M);
      
      //add up 3x3 inertia tensors
      Mat3f.add(destTensor,tensor1,tensor2);
      
      //H = H1 + h2 x
      Mat3f.skew(destTensor,H,tensor2,SpatialInertiaTensor.H);
      Mat3f.add(destTensor, H,tensor1,H);
    }
    
    /**
     * Idst = I1 + I2, I2 is spatial inertia tensor (13 elements)
     */
    public void addSpatialInertia(float []destTensor, int dstIndex, float[] tensor1, int index2, float[] tensor2, int index1)
    {
      //M = M1 + m2 1 
      Mat3f.setIdentity(destTensor,dstIndex+M);
      Mat3f.scale(destTensor, dstIndex+M,tensor2[index2+SpatialInertiaTensor.M]);
      Mat3f.add(destTensor,dstIndex+M,tensor1,index1+M);
      
      //add up 3x3 inertia tensors
      Mat3f.add(destTensor,dstIndex,tensor1,index1,tensor2,index2);
      
      //H = H1 + h2 x
      Mat3f.skew(destTensor,dstIndex+H,tensor2,index2+SpatialInertiaTensor.H);
      Mat3f.add(destTensor, dstIndex+H,tensor1,index1+H);
    }
    
    /**
     * vdest = I*v
     */
    public static void transformSpatialVec(float[] vdest, float[] tensor, float[] v)
    {
      //vdest = (Iw+Hv,Mv+H^Tw)
      
      //I w+Hv
      Mat3f.transform(vdest,3,tensor,0,v,0);
      Mat3f.transform(vdest,0,tensor,H,v,3);
      Vec3f.add(vdest, 0,vdest,0,vdest,3);
      
      float[] tmp=new float[3];
      //Mv+H^Tw)
      Mat3f.transform(tmp, 0,tensor,M,v,3);
      Mat3f.transformTranspose(vdest, 3,tensor,H,v,0);
      Vec3f.add(vdest,3,tmp,0);
    }
    
    /**
     * vdest = I*v
     */
    public static void transformSpatialVec(float[] vdest,int dstIndex, float[] tensor, int iIndex, float[] v, int vIndex)
    {
      //vdest = (Iw+Hv,Mv+H^Tw)
      
      //I w+Hv
      Mat3f.transform(vdest,3+dstIndex,tensor,iIndex,v,vIndex);
      Mat3f.transform(vdest,dstIndex,tensor,iIndex+H,v,3+vIndex);
      Vec3f.add(vdest,dstIndex,vdest,dstIndex,vdest,3+dstIndex);
      
      float[] tmp=new float[3];
      //Mv+H^Tw)
      Mat3f.transform(tmp, 0,tensor,iIndex+M,v,vIndex+3);
      Mat3f.transformTranspose(vdest, dstIndex+3,tensor,iIndex+H,v,vIndex);
      Vec3f.add(vdest,dstIndex+3,tmp,0);
    }
    
    /**
     * destTensor = tensor* srcTensor tensor^-1 
     */
    public static void xstarIXinv(float[] destTensor,float[] tensor,float[] srcTensor)
    {
      //abi(M,H,I) = E M E^T, E(H-rxM)E^T, E(I-rxH^T+(H-rxM)rx)E^T))
        
        //destTensor[M]=rx
        Mat3f.skew(destTensor,M,tensor,SpatialTransform.R);
        
        //destTensor[H]=rxM                             =destTensor[M] M 
        Mat3f.mul(destTensor, H,srcTensor,M);
        
        //destTensor[H]=H-rxM                           = H - destTensor[H] 
        Mat3f.sub(destTensor,H, srcTensor,H,destTensor,H);
        
        //destTensor[I]=(H-rxM)rx                       =destTensor[H] destTensor[I]
        Mat3f.mul(destTensor,I,destTensor,H,destTensor,M);
        
        //destTensor[M] = rx H^T                        =destTensor[M] H^T
        Mat3f.mulTransposeRight(destTensor,M,destTensor,M, srcTensor,H);
        
        //destTensor[I] = (H-rxM)rx-rxH^T               =destTensor[I]-destTensor[M]
        Mat3f.sub(destTensor,I,destTensor,M);
        
        //destTensor[I] = (H-rxM)rx-rxH^T+I
        Mat3f.add(destTensor,I, srcTensor,I);
        
        //destTensor[I] = ((H-rxM)rx-rxH^T+I)E^T
        Mat3f.mulTransposeRight(destTensor,I, destTensor, I, tensor, 0);
        
        //destTensor[I] = E((H-rxM)rx-rxH^T+I)E^T
        Mat3f.mul(destTensor,I, tensor, 0, destTensor,I);
        
        //destTensor[H] = E(H-rxM)
        Mat3f.mul(destTensor, H,tensor, 0, destTensor,H);
        
        //destTensor[H] = E(H-rxM)E^T
        Mat3f.mulTransposeRight(destTensor, H, destTensor,H, tensor, 0);
        
        //destTensor[M] = E M
        Mat3f.mul(destTensor,M, tensor, 0, srcTensor,M);
        
        //destTensor[M] = E M E^T
        Mat3f.mulTransposeRight(destTensor, M, destTensor,M, tensor, 0);
    }
    
    public static void setFromSpatialInertiaTensor(float[] destTensor, float[] spatialTensor)
    {
        Mat3f.set(destTensor, I, spatialTensor,SpatialInertiaTensor.I);
        Mat3f.skew(destTensor, H, spatialTensor, SpatialInertiaTensor.H);
        Mat3f.scale(destTensor, M, Mat3f.getIdentity(), 0, spatialTensor[SpatialInertiaTensor.M]);
    }
    
    /**
     * Tests for equality of vector components within epsilon.
     */
    public static boolean epsilonEquals(float[] tensor1, float[] tensor2, float epsilon)
    {
        return Mat3f.epsilonEquals(tensor1, M, tensor2,M,epsilon)
            && Mat3f.epsilonEquals(tensor1, H, tensor2, H, epsilon) 
            && Mat3f.epsilonEquals(tensor1, I, tensor2, I, epsilon);
    }
    
    /**
     * String representation
     */
    public static String toString(float[] tensor)
    {
        return "M: "+Mat3f.toString(tensor,M)+"\nH: "+Mat3f.toString(tensor,H)+"\nI: "+Mat3f.toString(tensor,I);
    }
}
