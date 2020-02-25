/*******************************************************************************
 * Copyright (C) 2009-2020 Human Media Interaction, University of Twente, the Netherlands
 *
 * This file is part of the Articulated Social Agents Platform BML realizer (ASAPRealizer).
 *
 * ASAPRealizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ASAPRealizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ASAPRealizer.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
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
