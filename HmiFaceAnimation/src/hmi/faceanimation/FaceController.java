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
