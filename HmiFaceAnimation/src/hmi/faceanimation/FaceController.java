/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General  License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General  License for more details.
 * 
 * You should have received a copy of the GNU General  License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
package hmi.faceanimation;


/**
 * The FaceController is the access point for deforming the face of an avatar,
 * just like VJoints are the accesspoint for deforming its body.
 * 
 * For now, the FaceController provides access to two ways of controlling a
 * face: - set morph targets - set FAPs Either method may do nothing at all if
 * such manipulations are not supported.
 * 
 * Because morph targets may also be non-face-related (body morphs), we only
 * have add and remove morphtarget. The calling application is responsible for
 * removing morph weights that it has added. The implementaion of FaceController
 * must maintain an administration of previously added morph targets...
 * 
 * FAPs are always only face related. Therefore, an implementation of
 * FaceController can suffice with implementing setMPEG4Configuration. This
 * abstract class implements the add & remove FAPs methods.
 * 
 * Implementations of this interface may control the face of an avatar rendered
 * in the HMI rendering environment, or send the commands onwards to another
 * rendering environment such as Greta or Ogre.
 * 
 * Note: to actually display the face configuration, you need to call copy()
 */
public interface FaceController extends MorphFaceController, MPEG4FaceController
{
}
