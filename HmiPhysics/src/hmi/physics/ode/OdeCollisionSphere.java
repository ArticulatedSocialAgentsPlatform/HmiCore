/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
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
