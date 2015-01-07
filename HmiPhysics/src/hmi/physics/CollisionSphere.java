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
package hmi.physics;

import hmi.math.Quat4f;
import hmi.math.Vec3f;

public class CollisionSphere implements CollisionShape
{
    public float radius;
    public float translation[] = new float[3];

    public CollisionSphere()
    {
        Vec3f.set(translation, 0, 0, 0);
        radius = 1;
    }

    public CollisionSphere(float r)
    {
        Vec3f.set(translation, 0, 0, 0);
        radius = r;
    }

    public CollisionSphere(float tr[], float r)
    {
        Vec3f.set(translation, tr);
        radius = r;
    }

    public void setRotation(float q[])
    {

    }

    public void getRotation(float q[])
    {
        Quat4f.setIdentity(q);
    }

    public void getTranslation(float v[])
    {
        Vec3f.set(v, translation);
    }

    public void setTranslation(float v[])
    {
        Vec3f.set(translation, v);
    }
}
