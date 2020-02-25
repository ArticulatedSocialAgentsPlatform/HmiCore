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
