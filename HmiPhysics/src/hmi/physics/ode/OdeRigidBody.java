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
import hmi.math.Vec3f;
import hmi.math.Vec4f;
import hmi.physics.CollisionShape;
import hmi.physics.Mass;
import hmi.physics.RigidBody;

import org.odejava.Body;
import org.odejava.Geom;
import org.odejava.PlaceableGeom;
import org.odejava.Space;
import org.odejava.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OdeRigidBody extends RigidBody
{
    private Body body;
    private World world;
    private Space space;
    private OdeMass mass;
    private int shapeNr = 0;
    private float tempv[] = new float[4];
    private float tempq[] = new float[4];

    private Logger logger = LoggerFactory.getLogger(OdeRigidBody.class.getName());

    /**
     * Constructor
     * 
     * @param n new name, id, sid
     * @param w ODE world
     * @param s ODE space
     */
    public OdeRigidBody(String n, World w, Space s)
    {
        world = w;
        space = s;
        id = n;
        sid = n;
        body = new Body(id, world);
        mass = new OdeMass(body.getdMass());
    }

    @Override
    public float getMass()
    {
        return mass.getMass();
    }

    public void setMass(OdeMass m)
    {
        mass = m;
        body.setMass(m.getOMass());
    }

    @Override
    public void setId(String n)
    {
        id = n;
        body.setName(n);

        for (CollisionShape cs : collisionShapes)
        {
            OdeCollisionShape ocs = (OdeCollisionShape) cs;
            ocs.setBodyName(n);
        }
    }

    @Override
    public void setTranslation(float x, float y, float z)
    {
        body.setPosition(x, y, z);
    }

    @Override
    public void setRotation(float w, float x, float y, float z)
    {
        body.setQuatWXYZ(w, x, y, z);
    }

    @Override
    public void setAngularVelocity(float x, float y, float z)
    {
        body.setAngularVel(x, y, z);
    }

    @Override
    public void setForce(float x, float y, float z)
    {
        checkForce(x, y, z);
        body.setForce(x, y, z);
    }

    protected void checkForce(float x, float y, float z)
    {
        if (Float.isNaN(x) || Float.isNaN(y) || Float.isNaN(z))
        {
            throw new RuntimeException("Force componenent with NaN force (" + x + "," + y + "," + z + ")");
        }
        if (Float.isInfinite(x) || Float.isInfinite(y) || Float.isInfinite(z))
        {
            throw new RuntimeException("Force componenent with infinite force (" + x + "," + y + "," + z + ")");
        }
    }

    @Override
    public void addForce(float x, float y, float z)
    {
        checkForce(x, y, z);
        body.addForce(x, y, z);
    }

    @Override
    public void addForceAtRelPos(float fx, float fy, float fz, float px, float py, float pz)
    {
        checkForce(fx, fy, fz);
        body.addForceAtRelPos(fx, fy, fz, px, py, pz);
    }

    @Override
    public void addForceAtPos(float fx, float fy, float fz, float px, float py, float pz)
    {
        checkForce(fx, fy, fz);
        body.addForceAtRelPos(fx, fy, fz, px, py, pz);
    }

    @Override
    public void setVelocity(float x, float y, float z)
    {
        body.setLinearVel(x, y, z);
    }

    @Override
    public void setTorque(float x, float y, float z)
    {
        checkForce(x, y, z);
        body.setTorque(x, y, z);
    }

    @Override
    public void addTorque(float x, float y, float z)
    {
        checkForce(x, y, z);
        body.addTorque(x, y, z);
    }

    /**
     * @return the body
     */
    public Body getBody()
    {
        return body;
    }

    @Override
    public void getTranslation(float[] pos)
    {
        body.getPosition(pos);
    }

    @Override
    public void getTranslation(float[] pos, int idx)
    {
        body.getPosition(tempv);
        Vec3f.set(pos, idx, tempv, 0);
    }

    /*
     * @Override public CollisionShape getCollisionShape() { return shape; }
     * 
     * public PlaceableGeom getCollisionGeom() { return shape.geom; }
     * 
     * public void setCollisionGeom(PlaceableGeom g) { if(shape.geom!=null) { space.remove(shape.geom); body.removeGeom(shape.geom); } shape.geom = g;
     * space.add(g); body.addCollisionGeom(g); }
     */

    private void addCollisionGeom(PlaceableGeom g)
    {
        space.add(g);
        body.addCollisionGeom(g);
    }

    private void removeCollisionGeom(PlaceableGeom g)
    {
        space.remove(g);
        body.removeGeom(g);
    }

    @Override
    public void getRotation(float[] result)
    {
        body.getQuatWXYZ(result);
    }

    @Override
    public void getRotation(float[] result, int idx)
    {
        body.getQuatWXYZ(tempv);
        Vec4f.set(result, idx, tempv, 0);
    }

    @Override
    public void getAngularVelocity(float[] result)
    {
        body.getAngularVel(result);
    }

    @Override
    public void getAngularVelocity(float[] vc, int vcIndex)
    {
        body.getAngularVel(tempv);
        Vec3f.set(vc, vcIndex, tempv, 0);
    }

    @Override
    public void getVelocity(float[] result)
    {
        body.getLinearVel(result);
    }

    @Override
    public void getVelocity(float[] result, int idx)
    {
        body.getLinearVel(tempv);
        Vec3f.set(result, idx, tempv, 0);
    }

    @Override
    public void adjustMass(float m)
    {
        mass.adjustMass(m, body);
    }

    @Override
    public OdeCollisionBox addBox(float[] halfExtends)
    {
        OdeCollisionBox b = new OdeCollisionBox(halfExtends, id + "_collision" + shapeNr);
        addCollisionGeom(b.root);
        collisionShapes.add(b);
        shapeNr++;
        return b;
    }

    @Override
    public OdeCollisionBox addBox(float[] q, float[] tr, float[] halfExtends)
    {
        OdeCollisionBox b = new OdeCollisionBox(q, tr, halfExtends, id + "_collision" + shapeNr);
        addCollisionGeom(b.root);
        collisionShapes.add(b);
        shapeNr++;
        return b;
    }

    @Override
    public OdeCollisionCapsule addCapsule(float[] q, float[] tr, float radius, float height)
    {
        OdeCollisionCapsule c = new OdeCollisionCapsule(q, tr, radius, height, id + "_collision" + shapeNr);
        addCollisionGeom(c.root);
        collisionShapes.add(c);
        shapeNr++;
        return c;
    }

    @Override
    public OdeCollisionCapsule addCapsule(float radius, float height)
    {
        OdeCollisionCapsule c = new OdeCollisionCapsule(radius, height, id + "_collision" + shapeNr);
        addCollisionGeom(c.root);
        collisionShapes.add(c);
        shapeNr++;
        return c;
    }

    @Override
    public OdeCollisionSphere addSphere(float[] tr, float radius)
    {
        OdeCollisionSphere s = new OdeCollisionSphere(tr, radius, id + "_collision" + shapeNr);
        addCollisionGeom(s.root);
        collisionShapes.add(s);
        shapeNr++;
        return s;
    }

    @Override
    public OdeCollisionSphere addSphere(float radius)
    {
        OdeCollisionSphere s = new OdeCollisionSphere(radius, id + "collision" + shapeNr);
        addCollisionGeom(s.root);
        collisionShapes.add(s);
        shapeNr++;
        return s;
    }

    @Override
    public void getCOM(float[] com)
    {
        mass.getCOM(com);
    }

    @Override
    public void getInertiaTensor(float[] I)
    {
        mass.getInertiaTensor(I);
    }

    @Override
    public void setCOM(float[] com)
    {
        mass.setCOM(com, body);
    }

    @Override
    public void setInertiaTensor(float[] I)
    {
        mass.setInertiaTensor(I, body);
    }

    @Override
    public void removeCollisionShape(CollisionShape s)
    {
        OdeCollisionShape os = (OdeCollisionShape) s;
        removeCollisionGeom(os.getRoot());
        collisionShapes.remove(s);
    }

    @Override
    public void clear()
    {
        super.clear();
        logger.debug("Removing body {}", body.getName());

        for (Geom g : body.getGeoms())
        {
            space.remove(g);
        }
        if (world.containsBody(body.getName()))
        {
            world.deleteBody(body);
        }
    }

    @Override
    public void getPointRelPosition(float[] dst, float[] point)
    {
        body.getRelPointPos(point[0], point[1], point[2], dst);
    }

    @Override
    public void getPointVelocity(float[] dst, float[] point)
    {
        body.getPointVel(point[0], point[1], point[2], dst);
    }

    @Override
    public void getRelativePointVelocity(float[] dst, float[] point)
    {
        body.getRelPointVel(point[0], point[1], point[2], dst);
    }

    @Override
    public void addRelTorque(float x, float y, float z)
    {
        checkForce(x, y, z);
        body.addRelTorque(x, y, z);
    }

    @Override
    public void getTorque(float torque[])
    {
        body.getTorque(torque);
    }

    @Override
    public void getForce(float force[])
    {
        body.getForce(force);
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        body.setEnabled(enabled);
        for (Geom g : body.getGeoms())
        {
            g.setEnabled(enabled);
        }
    }

    @Override
    public void addRelForce(float x, float y, float z)
    {
        getRotation(tempq);
        Vec3f.set(tempv, x, y, z);
        Quat4f.transformVec3f(tempq, tempv);
        addForce(tempv);
    }

    @Override
    public void addRelForceAtRelPos(float fx, float fy, float fz, float px, float py, float pz)
    {
        body.addRelForceAtRelPos(fx, fy, fz, px, py, pz);
    }

    @Override
    public void rotateInertiaTensor(float[] q)
    {
        mass.rotate(q);
    }

    @Override
    public void translateInertiaTensor(float[] v)
    {
        mass.translate(v);
    }

    @Override
    public Mass createMass()
    {
        return new OdeMass();
    }
}
