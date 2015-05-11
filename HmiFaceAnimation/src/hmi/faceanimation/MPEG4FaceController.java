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

import hmi.faceanimation.model.MPEG4Configuration;

/**
 * Controls the face using MPEG4
 * @author hvanwelbergen
 *
 */
public interface MPEG4FaceController
{
    /**
     * Directly set a FAP configuration on the face. FAPs that are assigned NULL
     * values will not be modified at all. All other FAPs will be completely
     * overwritten with the new configuration, i.e., no blending is done.
     */
    void setMPEG4Configuration(MPEG4Configuration config);

    void addMPEG4Configuration(MPEG4Configuration config);

    void removeMPEG4Configuration(MPEG4Configuration config);
    
    /**
     * Do actually apply the current MPEG4 configurations to the face
     */
    void copy();
}
