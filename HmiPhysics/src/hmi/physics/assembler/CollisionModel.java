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
package hmi.physics.assembler;

import hmi.graphics.collada.Box;
import hmi.graphics.collada.Capsule;
import hmi.graphics.collada.Rotate;
import hmi.graphics.collada.Shape;
import hmi.graphics.collada.Sphere;
import hmi.graphics.collada.Translate;
import hmi.math.Quat4f;
import hmi.math.Vec3f;
import hmi.physics.CollisionBox;
import hmi.physics.CollisionCapsule;
import hmi.physics.CollisionShape;
import hmi.physics.CollisionSphere;
import hmi.physics.RigidBody;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

/**
 * The collision shape description
 * @author Herwin
 */
public class CollisionModel extends XMLStructureAdapter
{
    private RigidBody body;

    public CollisionModel(RigidBody b)
    {
        body = b;
    }

    protected void addShape(Shape s)
    {
        float q[] = new float[4];

        float tr[] = new float[3];
        Quat4f.setIdentity(q);
        Vec3f.set(tr, 0, 0, 0);
        if (s.getRotate() != null)
        {
            Quat4f.set(q, s.getRotate().getRotationQuat4f());
        }
        if (s.getTranslate() != null)
        {
            Vec3f.set(tr, s.getTranslate().getTranslation());
        }

        if (s.getBox() != null)
        {
            body.addBox(q, tr, s.getBox().getHalfExtends().getXyz());
        }
        else if (s.getSphere() != null)
        {
            body.addSphere(tr, s.getSphere().getRadius().getRadius());
        }
        else if (s.getCapsule() != null)
        {
            body.addCapsule(q, tr, s.getCapsule().getRadius().getRadius(), s.getCapsule().getHeight().getHeight());
        }
    }

    @Override
    public boolean decodeAttribute(String attrName, String attrValue, XMLTokenizer tokenizer)
    {
        return decodeAttribute(attrName, attrValue);
    }

    @Override
    public void decodeContent(XMLTokenizer tokenizer) throws java.io.IOException
    {
        while (tokenizer.atSTag())
        {
            String tag = tokenizer.getTagName();
            if (tag.equals(Shape.xmlTag()))
            {
                addShape(new Shape(null, tokenizer));
            }
            else
            {
                System.err.println("CollisionModel: skip : " + tokenizer.getTagName());
                tokenizer.skipTag();
            }
        }
    }

    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt)
    {
        Shape colladaShape;
        for (CollisionShape s : body.getCollisionShapes())
        {
            colladaShape = new Shape();

            // set rotation
            float q[] = new float[4];
            s.getRotation(q);
            if (!Quat4f.isIdentity(q))
            {
                Rotate r = new Rotate();
                float aa[] = new float[4];
                Quat4f.setAxisAngle4fFromQuat4f(aa, q);
                r.setAxisAngleRadians(aa);
                colladaShape.setRotate(r);
            }

            // set translation

            float[] ta = new float[3];
            s.getTranslation(ta);

            // Vec3f.set(t.xyz,s.getTranslation());
            if (!Vec3f.equals(ta, 0, 0, 0))
            {
                Translate t = new Translate();
                t.setTranslation(ta);
                colladaShape.setTranslate(t);
            }

            // set collision shape specific info
            if (s instanceof CollisionBox)
            {
                CollisionBox cb = (CollisionBox) s;
                colladaShape.setBox(new Box());
                Vec3f.set(colladaShape.getBox().getHalfExtends().getXyz(), cb.halfExtends);
            }
            else if (s instanceof CollisionSphere)
            {
                CollisionSphere cs = (CollisionSphere) s;
                colladaShape.setSphere(new Sphere());
                colladaShape.getSphere().getRadius().setRadius(cs.radius);
            }
            else if (s instanceof CollisionCapsule)
            {
                CollisionCapsule cc = (CollisionCapsule) s;
                colladaShape.setCapsule(new Capsule());
                colladaShape.getCapsule().getHeight().setHeight(cc.height);
                colladaShape.getCapsule().getRadius().setRadius(cc.radius);
            }
            appendNewLine(buf);
            colladaShape.appendXML(buf, fmt);
        }
        return buf;
    }

    @Override
    public StringBuilder appendAttributeString(StringBuilder buf)
    {
        return buf;
    }

    @Override
    public String getXMLTag()
    {
        return "CollisionModel";
    }
}
