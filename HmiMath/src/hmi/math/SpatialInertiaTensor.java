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
 * Efficient implementation of the 6x6 spatial inertia tensor 
 * As defined in
 * 
 * Rigid Body Dynamics Algorithms
 * Roy Featherstone
 * 2007
 * 
 * using a 13-element float array
 * @author Herwin van Welbergen
 */
public final class SpatialInertiaTensor
{
    public static final int I = 0;
    public static final int H = 9;
    public static final int M = 12;
    
    private SpatialInertiaTensor(){}
    
    /**
     * Returns a new float[13] array with zero components
     */
    public static float[] getSpatialInertiaTensor() 
    {
       return new float[13];
    }
    
    /**
     * Sets the tensor, rotI is the rotational inertia tensor at the center of mass C
     * pos is the vector OC, with O the origin of the body
     */
    public static void set(float[] tensor, float[] rotI, float[] pos, float mass)
    {
        //tensor = rotI - m * cx * cx        
        //cx * cx
        Mat3f.skew(tensor, pos);
        Mat3f.mul(tensor, tensor);
        //-m * cx * cx
        Mat3f.scale(tensor, -mass);
        //tensor = rotI - m * cx * cx
        Mat3f.add(tensor,rotI);
        
        
        tensor[M]=mass;
        
        // H = M * c
        Vec3f.set(tensor,H,pos,0);
        Vec3f.scale(mass,tensor,H);
    }
    
    /**
     * Sets the tensor, rotI is the rotational inertia tensor at the center of mass C
     * pos is the vector OC, with O the origin of the body
     */
    public static void set(float[] tensor, int iIndex, float[] rotI, float[] pos, float mass)
    {
        //tensor = rotI - m * cx * cx        
        //cx * cx
        Mat3f.skew(tensor, iIndex, pos, 0);
        Mat3f.mul(tensor, iIndex, tensor, iIndex);
        //-m * cx * cx
        Mat3f.scale(tensor, iIndex, -mass);
        //tensor = rotI - m * cx * cx
        Mat3f.add(tensor,iIndex, rotI,0);
        
        
        tensor[iIndex+M]=mass;
        
        // H = M * c
        Vec3f.set(tensor,iIndex+H,pos,0);
        Vec3f.scale(mass,tensor,iIndex+H);
    }
    
    /**
     * Sets the tensor with the coordinate frame at the center of mass, that is, c = 0
     * rotI is the rotational inertia tensor at the center of mass
     */
    public static void set(float[] tensor, float[] rotI, float mass)
    {
        Mat3f.set(tensor,rotI);
        Vec3f.set(tensor,H,0.0f,0.0f,0.0f);
        tensor[M]=mass;
    }
    
    /**
     * Sets the tensor with the coordinate frame at the center of mass, that is, c = 0
     * rotI is the rotational inertia tensor at the center of mass
     */
    public static void set(float[] tensor, int iIndex, float[] rotI, float mass)
    {
        Mat3f.set(tensor,iIndex, rotI,0);
        Vec3f.set(tensor,iIndex+H,0.0f,0.0f,0.0f);
        tensor[M+iIndex]=mass;
    }
    
    /**
     * vdest = tensor*a 
     */
    public static void transformSpatialVec(float[] vdest, float[] tensor, float[] a)
    {
        //fv(tensor*aw + h x av, m*av - h x aw 
        
        //tensor * aw
        Mat3f.transform(tensor, vdest, a);
        
        //h x av
        Vec3f.cross(vdest, 3, tensor,H, a,3);
        
        //I * aw + h x av
        Vec3f.add(vdest,0,vdest,3);
        
        //h x aw
        Vec3f.cross(vdest,3,tensor,H,a,0);
        
        //-h x aw
        Vec3f.scale(-1f, vdest,3);
        
        //-h x aw + m * av 
        Vec3f.scaleAdd(vdest,3,tensor[M],a,3);
    }
    
    
    /**
     * vdest = I*a 
     */
    public static void transformSpatialVec(float[] vdest, int dstIndex, float[] tensor, int iIndex, float[] a, int aIndex)
    {
        //fv(tensor*aw + h x av, m*av - h x aw) 
        
        //I * aw
        Mat3f.transform(tensor, iIndex, vdest, dstIndex, a, aIndex);
        
        //h x av
        Vec3f.cross(vdest, dstIndex+3, tensor,iIndex+H, a,aIndex+3);
        
        //I * aw + h x av
        Vec3f.add(vdest,dstIndex,vdest,dstIndex+3);
        
        //h x aw
        Vec3f.cross(vdest,dstIndex+3,tensor,iIndex+H,a,aIndex);
        
        //-h x aw
        Vec3f.scale(-1f, vdest,dstIndex+3);
        
        //-hx x aw + m * av
        Vec3f.scaleAdd(vdest,dstIndex+3,tensor[iIndex+M],a,aIndex+3);
    }
    
    /**
     * Idest = I1 + I2
     */
    public void add(float[] destTensor,float[] tensor1, float[] tensor2)
    {
      destTensor[M]=tensor1[M]+tensor2[M];
      Vec3f.add(destTensor,H,tensor1,H,tensor2,H);
      Mat3f.add(destTensor, tensor1, tensor2);
    }
    
    /**
     * Idest = I1 + I2
     */
    public void add(float[] destTensor, int dstIndex, float[] tensor1, int index1, float[] tensor2, int index2)
    {
      destTensor[M+dstIndex]=tensor1[M+index1]+tensor2[M+index2];
      Vec3f.add(destTensor,H+dstIndex,tensor1,H+index1,tensor2,H+index2);
      Mat3f.add(destTensor, dstIndex, tensor1, index1, tensor2, index2);
    }
    
    /**
     * String representation
     */
    public static String toString(float[] tensor)
    {
        return "M: "+tensor[M]+"\nH: "+Vec3f.toString(tensor,H)+"\nI: "+Mat3f.toStringTabbed(tensor,I);
    }
}
