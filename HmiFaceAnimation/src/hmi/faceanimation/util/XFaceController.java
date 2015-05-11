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
package hmi.faceanimation.util;

import hmi.faceanimation.FaceController;
import hmi.faceanimation.model.MPEG4Configuration;

import java.util.ArrayList;

/**
 * The FaceController is the access point for deforming the face of an avatar, just like VJoints are the accesspoint for deforming its body.
 * 
 * This implementation controls the face of an avatar displayed in the XFAce environment.
 * 
 * 
 */
public class XFaceController implements FaceController
{
    ArrayList<String> possibleFaceMorphTargetNames = new ArrayList<String>();
    /** store the current config, for add- and remove-mpeg4configuration */
    private MPEG4Configuration currentConfig = new MPEG4Configuration();
    private XfaceInterface xfaceInterface = null;
    /**
     * This constructor needs to get a link to the face and its morph targets! Maybe the GLScene, for setting morph targets, and some of Ronald Paul's
     * stuff, for controlling FAPs
     */
    public XFaceController(XfaceInterface xfi)
    {
      xfaceInterface = xfi;
    }

    public synchronized void setMPEG4Configuration(MPEG4Configuration config)
    {
        currentConfig = config;
    }

    public synchronized void addMPEG4Configuration(MPEG4Configuration config)
    {
        currentConfig.addValues(config);
    }

    public synchronized void removeMPEG4Configuration(MPEG4Configuration config)
    {
        currentConfig.removeValues(config);
    }

    public ArrayList<String> getPossibleFaceMorphTargetNames()
    {
        return possibleFaceMorphTargetNames;
    }

    public void setPossibleFaceMorphTargetNames(ArrayList<String> names)
    {
        possibleFaceMorphTargetNames = names;
    }

    /**
     * NOTE: this function should be called in some synchronisation; to ensure that the values are not changed during the copy method!
     */
    public synchronized void copy()
    {
      xfaceInterface.setConfiguration(currentConfig);

    }

    /*
     * ====================================================== Methods and attributes for maintaining a set of desired morph targets. - Add and remove
     * weight from each target using addMorphTargets() and removeMorphTargets() - have GLScene execute the current set of targets and weights by
     * calling doMorph() ==================================================
     */

    /**
     * The set of morph targets to be set by doMorph, maintained through addMorphTargets and removeMorphTargets
     */

    /** Add given weights for given morph targets to the list of desired targets */
    public synchronized void addMorphTargets(String[] targetNames, float[] weights)
    {

    }

    /** Remove given weights for given morph targets from the list of desired targets */
    public synchronized void removeMorphTargets(String[] targetNames, float[] weights)
    {
       
    }

    @Override
    public synchronized void setMorphTargets(String[] targetNames, float[] weights)
    {
                
    }

    @Override
    public float getCurrentWeight(String targetName)
    {
        return 0;
    }

}
