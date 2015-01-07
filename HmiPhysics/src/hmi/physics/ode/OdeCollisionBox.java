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

import hmi.physics.CollisionBox;

import org.odejava.GeomBox;
import org.odejava.GeomTransform;
import org.odejava.PlaceableGeom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OdeCollisionBox extends CollisionBox implements OdeCollisionShape
{
    public GeomBox box;
    public PlaceableGeom root;
    public String name;
    private GeomTransform transform = null;

    private Logger logger = LoggerFactory.getLogger(OdeCollisionBox.class.getName());

    public OdeCollisionBox(String n)
    {
        super();
        name = n;
        createBox();
    }

    public OdeCollisionBox(float[] hExtends, String n)
    {
        super(hExtends);
        name = n;
        createBox();
    }

    public OdeCollisionBox(float q[], float tr[], float[] hExtends, String n)
    {
        super(q, tr, hExtends);
        name = n;
        createBox();
    }

    private void createBox()
    {
        box = new GeomBox(name + "_box", 2 * halfExtends[0], 2 * halfExtends[1], 2 * halfExtends[2]);

        /*
         * removed, just always create the transform for easy run-time rotation/translation if(Vec3f.equals(translation, 0f,0f,0f) &&
         * Quat4f.isIdentity(rotation)) { root = box; } else {
         */
        transform = new GeomTransform(name + "_Geomtransform");
        box.setPosition(translation[0], translation[1], translation[2]);
        box.setQuatWXYZ(rotation[0], rotation[1], rotation[2], rotation[3]);
        transform.setEncapsulatedGeom(box);
        root = transform;
        // }
    }

    public PlaceableGeom getCollisionGeom()
    {
        // return box;
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
    public void setRotation(float[] q)
    {
        super.setRotation(q);
        box.setQuatWXYZ(rotation[0], rotation[1], rotation[2], rotation[3]);
    }

    @Override
    public void setTranslation(float[] v)
    {
        super.setTranslation(v);
        box.setPosition(translation[0], translation[1], translation[2]);
    }

    public void setBodyName(String bodyName)
    {
        int index = name.indexOf("_");
        name = name.substring(index);
        name = bodyName + name;
        box.setName(name + "_box");
        root.setName(name + "_root");
        logger.debug("new collision shape name: {}", name);
    }
}
