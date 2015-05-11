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
package hmi.animation.motiongraph;

import lombok.Getter;
import hmi.animation.SkeletonInterpolator;
import hmi.animation.VJoint;

/**
 * Motiongraph edge, containing an animation
 * @author herwinvw
 */
public class MGEdge
{
    @Getter
    private final SkeletonInterpolator motion;
    
    @Getter
    private final MGNode outgoingNode;
    
    @Getter
    private final MGNode incomingNode;
    
    public MGEdge(SkeletonInterpolator motion, MGNode incomingNode, MGNode outgoingNode)
    {
        this.motion = motion;
        this.outgoingNode = outgoingNode;
        this.incomingNode = incomingNode;
        incomingNode.addEdge(this);
    }
    
    public double getDuration()
    {
        return motion.getEndTime()-motion.getStartTime();
    }
    
    public void play(double t)
    {
        motion.time(motion.getStartTime()+t);
    }
    
    public void setTarget(VJoint human)
    {
        motion.setTarget(human);
    }
}
