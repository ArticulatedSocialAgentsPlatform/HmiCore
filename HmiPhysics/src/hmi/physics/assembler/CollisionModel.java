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
