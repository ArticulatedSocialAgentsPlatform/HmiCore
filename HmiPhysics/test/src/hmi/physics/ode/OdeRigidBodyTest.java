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

import org.junit.After;
import org.junit.Before;

import org.odejava.HashSpace;
import org.odejava.Odejava;
import org.odejava.Space;
import org.odejava.World;
import hmi.physics.AbstractRigidBodyTest;

/**
 * RigidBody unit tests on Ode implementation of RigidBody
 * @author Herwin
 */
public class OdeRigidBodyTest extends AbstractRigidBodyTest
{
    @Before
    public void setUp() throws Exception
    {
        Odejava.init();
        World world = new World();
        Space space = new HashSpace();
        rigidBody = new OdeRigidBody("body1", world, space);
    }

    @After
    public void tearDown() throws Exception
    {
        Odejava.close();
    }
}
