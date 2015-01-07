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

import hmi.physics.CollisionSphere;

import org.odejava.GeomSphere;
import org.odejava.GeomTransform;
import org.odejava.PlaceableGeom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OdeCollisionSphere extends CollisionSphere implements OdeCollisionShape
{
    private Logger logger = LoggerFactory.getLogger(OdeCollisionSphere.class.getName());

    public GeomSphere sphere;
    public PlaceableGeom root;
    public String name;

    public OdeCollisionSphere(String n)
    {
        super();
        name = n;
        createSphere();
    }

    public OdeCollisionSphere(float r, String n)
    {
        super(r);
        name = n;
        createSphere();
    }

    public OdeCollisionSphere(float tr[], float r, String n)
    {
        super(tr, r);
        name = n;
        createSphere();
    }

    private void createSphere()
    {
        sphere = new GeomSphere(name + "_sphere", radius);
        /*
         * removed, just always create the translation if(Vec3f.equals(translation, 0f,0f,0f)) { root = sphere; } else {
         */
        GeomTransform g = new GeomTransform(name + "_Geomtransform");
        sphere.setPosition(translation[0], translation[1], translation[2]);
        g.setEncapsulatedGeom(sphere);
        root = g;
        // }
    }

    public PlaceableGeom getCollisionGeom()
    {
        // return sphere;
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

    @Override
    public void setTranslation(float[] v)
    {
        super.setTranslation(v);
        sphere.setPosition(translation[0], translation[1], translation[2]);
    }

    public void setBodyName(String bodyName)
    {
        int index = name.indexOf("_");
        name = name.substring(index);
        name = bodyName + name;
        sphere.setName(name + "_sphere");
        root.setName(name + "_root");
        logger.debug("new collision shape name: {}", name);
    }
}
