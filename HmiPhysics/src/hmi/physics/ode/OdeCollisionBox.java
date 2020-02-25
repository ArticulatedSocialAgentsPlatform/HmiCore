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
