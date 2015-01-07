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
package hmi.physics.ode;
import hmi.math.Quat4f;
import hmi.physics.CollisionCapsule;

import org.odejava.GeomCapsule;
import org.odejava.GeomTransform;
import org.odejava.PlaceableGeom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class OdeCollisionCapsule extends CollisionCapsule implements OdeCollisionShape
{
    private Logger logger = LoggerFactory.getLogger(OdeCollisionCapsule.class.getName());
    public GeomCapsule capsule;
    public PlaceableGeom root;
    public String name;
    private GeomTransform g2 = null;
    private GeomTransform g1 = null;
    public OdeCollisionCapsule(String n)
    {
        super();
        name = n;
        createCapsule();
    }
    
    public OdeCollisionCapsule(float r, float h, String n)
    {
        super(r,h);       
        name = n;
        createCapsule();
    }
    
    public OdeCollisionCapsule(float q[], float tr[], float r, float h, String n)
    {
        super(q,tr,r,h);     
        name = n;
        createCapsule();
    }
    
    private void createCapsule()
    {
        capsule = new GeomCapsule(name+"_capsule",radius,height);
        
        //an Ode capsule is aligned with the Z-axis,
        //rotate the capsule 90 degrees on the x-axis, so it is aligned with the Y-axis, 
        //as desired by the collada specification
        float q[] = new float[4];
        float aa[] = new float[4];
        aa[0] = 1;
        aa[1] = 0;
        aa[2] = 0;
        aa[3] = (float)Math.PI*0.5f;
        Quat4f.setFromAxisAngle4f(q, aa);
        capsule.setQuatWXYZ(q[0],q[1],q[2],q[3]);
        
        g1 = new GeomTransform(name+"_Geomtransform1");        
        g1.setEncapsulatedGeom(capsule);
        /* removed, just always set a transform
        if(Vec3f.equals(translation, 0f,0f,0f) && Quat4f.isIdentity(rotation))
        {
            root = g1;
        }
        else
        {
        */
            g2 = new GeomTransform(name+"_Geomtransform2");
            g1.setPosition(translation[0],translation[1],translation[2]);
            g1.setQuatWXYZ(rotation[0],rotation[1],rotation[2],rotation[3]);
            g2.setEncapsulatedGeom(g1);
            root = g2;           
        //}    
    }

    @Override
    public void setRotation(float[] q)
    {
        super.setRotation(q);
        g1.setQuatWXYZ(rotation[0],rotation[1],rotation[2],rotation[3]);        
    }
    
    @Override
    public void setTranslation(float []v)
    {
        super.setTranslation(v);
        g1.setPosition(translation[0],translation[1],translation[2]);
    }
    
    public PlaceableGeom getCollisionGeom()
    {
        //return capsule;
        return root;
    }

    public PlaceableGeom getRoot()
    {
        return root;
    }
    
    @Override
    public String toString()
    {
        return name;
    }
    
    public void setBodyName(String bodyName)
    {
        int index = name.indexOf("_");
        name = name.substring(index);
        name = bodyName+name;
        capsule.setName(name+"_capsule");        
        g1.setName(name+"_geomtransform1");
        if(g2!=null)
        {
            g2.setName(name+"_geomtransform2");
        }
        logger.debug("new collision shape name: {}",name);
    }
}
