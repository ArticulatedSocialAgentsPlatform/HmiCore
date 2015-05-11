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
package hmi.animationui;

import hmi.animation.VJoint;
import hmi.animation.VJointUtils;

import java.util.Collection;
import java.util.Map;

/**
 * The controller handles input from the viewer and updates the vjoint structure
 * 
 * @author hvanwelbergen
 */
public class JointController implements RotationsController
{
    private final VJoint model;
    private JointView jv;

    public JointController(VJoint vj)
    {
        model = vj;
    }

    public JointView constructJointView()
    {
        jv = new JointView(this, VJointUtils.transformToSidList(model.getParts()));
        return jv;
    }

    /**
     * This method sets the joints of the model to the specified configurations.
     * It does not affect the sliders.
     */
    public void setJointRotations(Collection<JointRotationConfiguration> rotations)
    {
        for (JointRotationConfiguration rotConf : rotations)
        {
            model.getPart(rotConf.getJointName()).setRotation(rotConf.getQ());
        }        
    }

    /**
     * This method sets the slider of each affected joint to the values
     * specified in the JointRotationConfigurations. As a result of that, the
     * models joints adjust themselves to the sliders configurations.
     * 
     * @param rotations
     */
    public void setJointRotationConfigurations(Collection<JointRotationConfiguration> rotations)
    {
        jv.setJointRotationConfiguration(rotations);
    }

    public Collection<JointRotationConfiguration> getJointRotations()
    {
        return jv.getJointRotationConfigurations();
    }

    public Collection<JointRotationConfiguration> getSelectedJointRotations()
    {
        return jv.getSelectedJointRotationConfigurations();
    }

    /**
     * Sets the values of all sliders for the joints in <i>joints</i> to the
     * value of the corresponding joint.
     * 
     * @param righthandJoints
     */
    public void adjustSliderToModel(Collection<String> joints)
    {
        jv.adjustSliderToModel(model, joints);
    }
    
    /**
     * Excludes every joint from the animation.
     */
    public void deselectAll(){
    	jv.deselectAll();
    }
}
