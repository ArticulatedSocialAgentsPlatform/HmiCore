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

/**
 * A physical controller steers a set of required joints and desired joints. Each implementation should ensure that it is robust missing desired
 * joints.
 * 
 * @author welberge
 */
public interface PhysicalController
{
    /**
     * Updates the controller
     * 
     * @param timeDiff time since last update
     */
    void update(double timeDiff);

    /**
     * Gets the ids of controlled joints
     */
    Set<String> getRequiredJointIDs();

    /**
     * Gets the ids of joints the controller would like to steer if available
     */
    Set<String> getDesiredJointIDs();
    
    /**
     * Get the ids of the joints the controllers is steering
     * @return
     */
    Set<String> getJoints();

    /**
     * Clears out all previous time dependent variables
     */
    void reset();

    /**
     * Links the controller to a new set of physical joints
     * 
     * @param ph physical humanoid to link to
     */
    void setPhysicalHumanoid(PhysicalHumanoid ph);

    /**
     * Creates a copy of the controller
     */
    PhysicalController copy(PhysicalHumanoid ph);

    void setParameterValue(String name, String value) throws ControllerParameterException;

    /**
     * Get the parameter value of parameter name
     */
    String getParameterValue(String name) throws ControllerParameterNotFoundException;

    float getFloatParameterValue(String name) throws ControllerParameterNotFoundException;

    void setParameterValue(String name, float value) throws ControllerParameterException;
}
