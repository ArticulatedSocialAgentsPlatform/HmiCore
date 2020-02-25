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
package hmi.animation.motiongraph.graphutils;

import hmi.animation.motiongraph.Edge;
import hmi.animation.motiongraph.MotionGraph;
import hmi.animation.motiongraph.Node;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

interface Visitor
{
    void visit(Node n);
}

interface Updater
{
    void update();
}

interface HitVisited
{
    /**
     * When exploring outgoing edge e of node u, we hit visited node n 
     */
    void hitVisited(Node u, Node n, Edge e);
}

/**
 * <p>
 * Generic depth first search for a motiongraph. Visits all nodes once in a depth first manner. Starts at the first provided node. 
 * Visitor functions may be hooked up and are called before and after the search visits a new Node. 
 * The updater is called whenever the search explores a new island. HitVisited is called whenever 
 * the search hits an already visited Node.  
 * </p>
 * Based on the graph algorithms from<br>
 * CS 161 - Design and Analysis of Algorithms, Stanford, by Mark Zhandry<br>
 * @see <a href="http://crypto.stanford.edu/~zhandry/2012-Summer-CS161/">CS161</a>
 * @author hvanwelbergen
 */
public final class DepthFirstSearch
{
    private DepthFirstSearch()
    {
    }

    public static void search(List<Node> nodes, Visitor preVisitor, Visitor postVisitor, Updater updater, HitVisited hitVisited)
    {
        Set<Node> visited = new HashSet<>();
        for (Node n : nodes)
        {
            if (!visited.contains(n))
            {
                updater.update();
                explore(n, visited, preVisitor, postVisitor, hitVisited);
            }
        }
    }

    public static void search(MotionGraph g, Visitor preVisitor, Visitor postVisitor, Updater updater, HitVisited hitVisited)
    {
        search(g.getNodes(), preVisitor, postVisitor, updater, hitVisited);
    }

    public static void explore(Node u, Set<Node> visited, Visitor preVisitor, Visitor postVisitor, HitVisited hitVisited)
    {
        visited.add(u);
        preVisitor.visit(u);
        for (Edge e : u.getOutgoingEdges())
        {
            Node n = e.getEndNode();
            if (!visited.contains(n))
            {
                explore(n, visited, preVisitor, postVisitor, hitVisited);
            }
            else
            {
                hitVisited.hitVisited(u,n,e);
            }
        }
        postVisitor.visit(u);
    }
}
