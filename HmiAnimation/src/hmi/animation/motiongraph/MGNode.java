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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import lombok.Getter;

public class MGNode
{
    @Getter
    private List<MGEdge> outgoingEdges = new ArrayList<MGEdge>();
    private Random rand = new Random(System.currentTimeMillis());
    
    public void addEdge(MGEdge edge)
    {
        outgoingEdges.add(edge);
    }

    public void removeEdge(MGEdge remove)
    {
        outgoingEdges.remove(remove);
    }

    public void removeEdges(Collection<MGEdge> removeEdges)
    {
        outgoingEdges.removeAll(removeEdges);
    }

    public MGEdge randomEdge()
    {
        int index = (int) rand.nextInt(outgoingEdges.size());
        return outgoingEdges.get(index);
    }
}