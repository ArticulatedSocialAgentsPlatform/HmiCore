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
package hmi.physics.controller;

import hmi.physics.PhysicalHumanoid;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * A controller that does not, in fact, control any joints. It is there to be used in BML replacement groups: replace a left-arm controller with a
 * no_controller allows you to take over the arm for a while in the service of some gesture.
 * 
 * @author reidsma
 */
public class NoController implements PhysicalController
{
    private final Set<String> EMPTY = ImmutableSet.of();

    /**
     */
    public NoController()
    {
    }

    @Override
    public void reset()
    {
    }

    @Override
    public void update(double timeDiff)
    {
    }

    @Override
    public Set<String> getRequiredJointIDs()
    {
        return EMPTY;
    }

    @Override
    public Set<String> getDesiredJointIDs()
    {
        return EMPTY;
    }

    @Override
    public Set<String> getJoints()
    {
        return EMPTY;
    }

    @Override
    public void setPhysicalHumanoid(PhysicalHumanoid ph)
    {

    }

    @Override
    public PhysicalController copy(PhysicalHumanoid ph)
    {
        return new NoController();
    }

    @Override
    public String getParameterValue(String name) throws ControllerParameterNotFoundException
    {
        throw new ControllerParameterNotFoundException(name);
    }

    @Override
    public void setParameterValue(String name, String value) throws ControllerParameterNotFoundException
    {
        throw new ControllerParameterNotFoundException(name);
    }

    @Override
    public void setParameterValue(String name, float value) throws ControllerParameterNotFoundException
    {
        throw new ControllerParameterNotFoundException(name);
    }

    @Override
    public float getFloatParameterValue(String name) throws ControllerParameterNotFoundException
    {
        throw new ControllerParameterNotFoundException(name);
    }

}
