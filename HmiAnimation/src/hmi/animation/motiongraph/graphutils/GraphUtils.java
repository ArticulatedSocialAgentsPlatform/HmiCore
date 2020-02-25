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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.Data;
import lombok.Getter;

/**
 * Based on the graph algorithms from<br>
 * CS 161 - Design and Analysis of Algorithms, Stanford, by Mark Zhandry<br>
 * @see <a href="http://crypto.stanford.edu/~zhandry/2012-Summer-CS161/">CS161</a>
 * @author herwinvw
 *
 */
public final class GraphUtils
{
    private GraphUtils()
    {
    }

    public static final class NopVisitor implements Visitor
    {
        @Override
        public void visit(Node n)
        {

        }
    }

    public static final class NopUpdater implements Updater
    {
        @Override
        public void update()
        {

        }
    }

    public static final class NopHitVisited implements HitVisited
    {
        @Override
        public void hitVisited(Node u, Node n, Edge e)
        {

        }
    }

    public static MotionGraph reverse(MotionGraph mg)
    {
        Map<Integer, Node> nodesReverse = new HashMap<>();
        List<Edge> edgesReverse = new ArrayList<>();
        for (Node n : mg.getNodes())
        {
            Node nrev = new Node();
            nrev.setId(n.getId());
            nodesReverse.put(n.getId(), nrev);
        }
        for (Edge e : mg.getEdges())
        {
            Node startNode = nodesReverse.get(e.getEndNode().getId());
            Node endNode = nodesReverse.get(e.getStartNode().getId());
            Edge eReverse = new Edge(startNode, endNode, e.getMotion());
            eReverse.setId(e.getId());
            startNode.addOutgoingEdge(eReverse);
            endNode.addIncomingEdge(eReverse);
            edgesReverse.add(eReverse);
        }
        return new MotionGraph.Builder(edgesReverse, nodesReverse.values()).getInstance();
    }

    /**
     * For each node, finds its connected components number
     */
    public static Map<Node, Integer> getConnected(MotionGraph g)
    {
        class Marker implements Visitor, Updater
        {
            private int cc = 0;

            @Getter
            private Map<Node, Integer> connected = new HashMap<>();

            @Override
            public void update()
            {
                cc++;
            }

            @Override
            public void visit(Node n)
            {
                connected.put(n, cc);
            }
        }
        Marker marker = new Marker();
        DepthFirstSearch.search(g, marker, new NopVisitor(), marker, new NopHitVisited());
        return marker.getConnected();
    }

    public static Map<Node, Integer> getPostNumbers(MotionGraph g)
    {
        class PostCounter implements Visitor
        {
            @Getter
            Map<Node, Integer> post = new HashMap<>();
            int count = 0;

            @Override
            public void visit(Node n)
            {
                post.put(n, count);
                count++;
            }
        }
        PostCounter pc = new PostCounter();
        DepthFirstSearch.search(g, new NopVisitor(), pc, new NopUpdater(), new NopHitVisited());
        return pc.getPost();
    }

    @Data
    static final class SCCDAGEdge
    {
        final int start;
        final int end;
    }

    public static final class SCCDAGNode
    {
        @Getter
        final int id;

        public SCCDAGNode(int id)
        {
            this.id = id;
        }

        public void addIncomingEdge(SCCDAGEdge e)
        {
            incomingEdges.add(e);
        }

        public void addOutgoingEdge(SCCDAGEdge e)
        {
            outgoingEdges.add(e);
        }

        @Getter
        private Set<SCCDAGEdge> incomingEdges = new HashSet<>();

        @Getter
        private Set<SCCDAGEdge> outgoingEdges = new HashSet<>();
    }

    @Data
    public static final class SCCDAG
    {
        final List<SCCDAGNode> nodes;
        final Map<Node, Integer> innerNodeMap; // maps graph node to SCC node
        final Set<SCCDAGEdge> edges;

        public Set<Node> getNodes(int sccId)
        {
            Set<Node> nodes = new HashSet<>();
            for (Entry<Node, Integer> entry : innerNodeMap.entrySet())
            {
                if (entry.getValue() == sccId)
                {
                    nodes.add(entry.getKey());
                }
            }
            return nodes;
        }
    }

    public static SCCDAG getStronglyConnectedComponents(MotionGraph g)
    {
        Map<Node, Integer> postMap = getPostNumbers(reverse(g));
        List<Entry<Node, Integer>> postEntries = new ArrayList<>(postMap.entrySet());
        Collections.sort(postEntries, new Comparator<Entry<Node, Integer>>()
        {
            @Override
            public int compare(Entry<Node, Integer> arg0, Entry<Node, Integer> arg1)
            {
                if (arg0.getValue() < arg1.getValue()) return 1;
                if (arg0.getValue() > arg1.getValue()) return -1;
                return 0;
            }
        });

        List<Node> nodes = new ArrayList<>();
        for (Entry<Node, Integer> entry : postEntries)
        {
            nodes.add(g.getNode(entry.getKey().getId()));
        }

        class Marker implements Visitor, Updater, HitVisited
        {
            @Getter
            private int cc = 0;

            @Getter
            private Map<Node, Integer> connected = new HashMap<>();

            @Getter
            private Set<SCCDAGEdge> edges = new HashSet<>();

            @Override
            public void update()
            {
                cc++;
            }

            @Override
            public void visit(Node n)
            {
                connected.put(n, cc);
            }

            @Override
            public void hitVisited(Node u, Node n, Edge e)
            {
                if (connected.containsKey(n))
                {
                    int scc = connected.get(n);
                    if (scc != cc)
                    {
                        edges.add(new SCCDAGEdge(cc, scc));
                    }
                }
            }
        }
        Marker marker = new Marker();
        DepthFirstSearch.search(nodes, new NopVisitor(), marker, marker, marker);
        List<SCCDAGNode> dagnodes = new ArrayList<>();
        for (int i = 1; i <= marker.getCc(); i++)
        {
            SCCDAGNode n = new SCCDAGNode(i);
            for (SCCDAGEdge e : marker.getEdges())
            {
                if (e.getStart() == i)
                {
                    n.addOutgoingEdge(e);
                }
                if (e.getEnd() == i)
                {
                    n.addIncomingEdge(e);
                }
            }
            dagnodes.add(n);
        }
        return new SCCDAG(dagnodes, marker.getConnected(), marker.getEdges());
    }

    /**
     * Prune all Sink SCCs from the motiongraph that have less than pruneSize nodes
     */
    public final static void pruneSinkSCCs(MotionGraph mg, int pruneSize)
    {
        boolean pruned = false;
        while (!pruned)
        {
            pruned = true;
            SCCDAG dag = getStronglyConnectedComponents(mg);
            for (SCCDAGNode n : dag.nodes)
            {
                Set<Node> innerNodes = dag.getNodes(n.getId());
                if (n.getOutgoingEdges().isEmpty() && innerNodes.size() < pruneSize)
                {
                    mg.removeNodes(innerNodes);
                    pruned = false;
                }
            }
        }
    }
}
