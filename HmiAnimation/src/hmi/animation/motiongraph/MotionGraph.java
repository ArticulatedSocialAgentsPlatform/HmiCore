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
package hmi.animation.motiongraph;

import hmi.animation.SkeletonInterpolator;
import hmi.animation.motiongraph.alignment.Alignment;
import hmi.animation.motiongraph.alignment.IAlignment;
import hmi.animation.motiongraph.blending.Blend;
import hmi.animation.motiongraph.blending.IBlend;
import hmi.animation.motiongraph.metrics.Equals;
import hmi.animation.motiongraph.metrics.IDistance;
import hmi.animation.motiongraph.metrics.IEquals;
import hmi.animation.motiongraph.metrics.JointAngles;
import hmi.animation.motiongraph.split.DefaultSplit;
import hmi.animation.motiongraph.split.ISplit;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import lombok.Getter;

/**
 * Use {@link Builder} to create an Instance.
 * <p>
 * TODO: Frames->Seconds
 * <p>
 * Created by Zukie on 15/06/15.
 * <p>
 * @author Zukie
 */
public final class MotionGraph implements IMotionGraph {

    /**
     * Number of Frames to be blended.
     * TODO: set
     */
    public static final int DEFAULT_BLENDING_FRAMES = 100;
    /**
     * Max distance suitable for blending.
     * TODO: set
     */
    public static final double DEFAULT_THRESHOLD = 20;

    /**
     * If all motions should be added normal.
     * TODO: set
     */
    public static final boolean NORMAL = true;
    /**
     * If all Motions should also be added mirrored.
     * TODO: set
     * 
     */
    public static final boolean MIRRORED = false;

    @Getter
    private final List<Edge> edges;
    
    @Getter
    private final List<Node> nodes;

    private final IAlignment align;
    private final IBlend blending;
    
    /**
     * Random-Generator used in {@link #next()}.
     */
    private final IDistance metric;
    private final Random r = new Random();
    private Node currentNode;

    public Node getNode(int id)
    {
        for(Node n:nodes)
        {
            if(n.getId()==id)
            {
                return n;
            }
        }        
        return null;
    }
    
    public Edge getEdge(int id)
    {
        for(Edge e:edges)
        {
            if(e.getId()==id)
            {
                return e;
            }
        }        
        return null;
    }
    
    public MotionGraph(Collection<Edge> edges, Collection<Node>nodes, IAlignment align, IDistance metric, IBlend blending, ISplit split) {
        this.align = align;
        this.metric = metric;
        this.blending = blending;
        this.edges = new LinkedList<>(edges);
        this.nodes = new LinkedList<>(nodes);
    }
    
    public MotionGraph(List<SkeletonInterpolator> motions, IAlignment align, IDistance metric, IBlend blending, ISplit split) {
        edges = new LinkedList<>();
        nodes = new LinkedList<>();
        if (motions == null || motions.isEmpty()) {
            throw new IllegalArgumentException("motions null or empty.");
        }
        if (align == null) {
            throw new IllegalArgumentException("No IAlignment specified.");
        }
        if (metric == null) {
            throw new IllegalArgumentException("No IDistance specified.");
        }
        if (blending == null) {
            throw new IllegalArgumentException("No IBlend specified.");
        }
        if (split == null) {
            throw new IllegalArgumentException("No ISplit specified.");
        }

        this.align = align;
        this.metric = metric;
        this.blending = blending;
        
        this.init(motions, split);
    }

    /**
     * Initialise MotionGraph. Creates Edges vor every Motion and mirrors them.
     * <p>
     * @param motions
     */
    private void init(List<SkeletonInterpolator> motions, ISplit split) {

        for (SkeletonInterpolator sp : motions) {

            if (NORMAL) {
                Edge newEdge = new Edge(sp);
                Node startNode = new Node(null, newEdge);
                Node endNode = new Node(newEdge, null);

                nodes.add(startNode);
                nodes.add(endNode);
                edges.add(newEdge);
            }

            // Mirror every motion
            if (MIRRORED) {
                SkeletonInterpolator newSp = new SkeletonInterpolator(sp);
                newSp.mirror();

                Edge mirroredEdge = new Edge(newSp);
                Node mirroredStartNode = new Node(null, mirroredEdge);
                Node mirroredEndNode = new Node(mirroredEdge, null);

                nodes.add(mirroredEndNode);
                nodes.add(mirroredStartNode);
                edges.add(mirroredEdge);
            }

        }

        System.out.println("NODES BEFORE: " + nodes.size());
        System.out.println("EDGES BEFORE: " + edges.size());

        this.connectMotions();
        this.split(split);
        this.createBlends();
        this.prune();
        System.out.println("NODES AFTER: " + nodes.size());
        System.out.println("EDGES AFTER: " + edges.size());
    }

    /**
     * Randomly splits Motions in the graph. TODO: Create Split-Class.
     */
    private void split(ISplit split) {
        List<Edge> oldEdges = new LinkedList<>(edges);
        for (Edge oldEdge : oldEdges) {

            Node startNode = oldEdge.getStartNode();
            Node endNode = oldEdge.getEndNode();
            removeEdge(oldEdge);

            List<SkeletonInterpolator> splits = split.split(oldEdge.getMotion());

            for (int i = 0; i < splits.size(); i++) {
                SkeletonInterpolator get = splits.get(i);
                Edge newEdge = new Edge(get);
                newEdge.setStartNode(startNode);

                if (i == splits.size() - 1) {
                    startNode = endNode;
                } else {
                    startNode = new Node();
                    this.nodes.add(startNode);
                }
                newEdge.setEndNode(startNode);
                this.edges.add(newEdge);
            }

        }
    }

    /**
     * Remove Edge from MotionGraph and it's nodes.
     * <p/>
     *
     * @param edge
     */
    private void removeEdge(Edge edge) {
        this.edges.remove(edge);
        edge.getStartNode().getIncomingEdges().remove(edge);
        edge.getStartNode().getOutgoingEdges().remove(edge);

        edge.getEndNode().getIncomingEdges().remove(edge);
        edge.getEndNode().getOutgoingEdges().remove(edge);
    }

    /**
     * Removes all Nodes, which have no successors.
     */
    private void prune() {

        boolean pruned = true;

        do {
            pruned = true;
            for (Iterator<Node> iterator = nodes.iterator(); iterator.hasNext();) {
                Node node = iterator.next();
                if (!node.hasNext()) {
                    while (!node.getIncomingEdges().isEmpty()) {
                        removeEdge(node.getIncomingEdges().get(0));
                    }

                    pruned = false;
                    iterator.remove();
                    break;
                }
            }
        } while (!pruned);

    }

    public void removeNodes(Set<Node> nodes)
    {
        for(Node n:nodes)
        {
            edges.removeAll(n.getIncomingEdges());
            edges.removeAll(n.getOutgoingEdges());
        }
        this.nodes.removeAll(nodes);
    }
    /**
     * Returns next motion to be displayed.
     * <p>
     * @return Skeletoninterpolator next.
     */
    @Override
    public SkeletonInterpolator next() {
        if (currentNode == null) {
            this.currentNode = nodes.get(r.nextInt(nodes.size()));
        }

        Edge currentEdge = currentNode.getOutgoingEdges().get(r.nextInt(currentNode.getOutgoingEdges().size()));
        currentEdge.played++;
        if (currentEdge.isBlend()) {
            System.out.println("Edge: " + currentEdge.getId() + " p: " + currentEdge.played + " (blend)");
        } else {
            System.out.println("Edge: " + currentEdge.getId() + " p: " + currentEdge.played);
        }

        SkeletonInterpolator next = currentEdge.getMotion();

        if (currentEdge.getEndNode().hasNext()) {
            currentNode = currentEdge.getEndNode();
            return next;

        } else {
            this.currentNode = null;
            next();
        }

        return null;

    }

    /**
     * Reconnect all Motions that have been cut in xml-format. Will not be needed in final implementation
     */
    private void connectMotions() {

        IEquals equals = new Equals();

        for (Edge start : edges) {
            for (Edge end : edges) {
                if (equals.startEndEquals(start.getMotion(), end.getMotion())) {
                    Node deletedNode = end.getStartNode();
                    nodes.remove(deletedNode);
                    deletedNode.getOutgoingEdges().remove(end);
                    end.setStartNode(start.getEndNode());

                }
            }
        }

    }

    /**
     * Connect all Motions that are similar enough with blends.
     */
    private void createBlends() {
        List<Node> starts = new LinkedList<>();
        for (Node node : nodes) {
            if (!node.getIncomingEdges().isEmpty()) {
                starts.add(node);
            }
        }
        List<Node> ends = new LinkedList<>();
        for (Node node : nodes) {
            if (node.hasNext()) {
                ends.add(node);
            }
        }

        for (Node start : starts) {
            for (Node end : ends) {
                if (start == end) {
                    //motions already connected
                    continue;
                }

                if (start.getIncomingEdges().get(0).getMotion().size() >= DEFAULT_BLENDING_FRAMES
                        && end.getOutgoingEdges().get(0).getMotion().size() >= DEFAULT_BLENDING_FRAMES) {

                    if (metric.distance(start.getIncomingEdges().get(0).getMotion(),
                            end.getOutgoingEdges().get(0).getMotion(), DEFAULT_BLENDING_FRAMES) <= DEFAULT_THRESHOLD) {
                        createBlending(start.getIncomingEdges().get(0), end.getOutgoingEdges().get(0));
                    }
                }
            }

        }
    }

    /**
     * Creates Connection between first and second.
     * <p>
     * If first are longer than {@link #DEFAULT_BLENDING_FRAMES} Frames, first is splittet in two piece, with the
     * blended Part {@link #DEFAULT_BLENDING_FRAMES} long. Else, the Start-Node of first is used of the StartNode of the
     * Blending
     * <p>
     * Same for second, excpet that its End-node is used, if it isn't splitet, as the blending-End-Node.
     * <p>
     * @param first first motion
     * @param second second motion
     */
    private void createBlending(Edge first, Edge second) {
        Node newEnd;
        Node newStart;
        SkeletonInterpolator blendEnd;
        SkeletonInterpolator blendStart;

        //Split first
        if (first.getMotion().size() > DEFAULT_BLENDING_FRAMES) {
            blendStart = first.getMotion().subSkeletonInterpolator(first.getMotion().size() - DEFAULT_BLENDING_FRAMES);
            Edge firstMotionPart2 = new Edge(blendStart);

            SkeletonInterpolator split1 = first.getMotion().subSkeletonInterpolator(0, first.getMotion().size() - DEFAULT_BLENDING_FRAMES);//could be length 0
            Edge firstMotionPart1 = new Edge(split1);

            first.getStartNode().addOutgoingEdge(firstMotionPart1);
            first.getEndNode().addIncomingEdge(firstMotionPart2);
            newStart = new Node(firstMotionPart1, firstMotionPart2);
            this.removeEdge(first);

            edges.add(firstMotionPart1);
            edges.add(firstMotionPart2);
            nodes.add(newStart);
        } else {
            newStart = first.getStartNode();
            blendStart = first.getMotion();
        }

        //split second
        if (second.getMotion().size() > DEFAULT_BLENDING_FRAMES) {

            blendEnd = second.getMotion().subSkeletonInterpolator(0, DEFAULT_BLENDING_FRAMES);
            Edge secondMotionPart1 = new Edge(blendEnd);

            SkeletonInterpolator split2 = second.getMotion().subSkeletonInterpolator(DEFAULT_BLENDING_FRAMES);//could be length 0
            Edge secondMotionPart2 = new Edge(split2);

            second.getStartNode().addOutgoingEdge(secondMotionPart1);
            second.getEndNode().addIncomingEdge(secondMotionPart2);
            newEnd = new Node(secondMotionPart1, secondMotionPart2);
            this.removeEdge(second);
            edges.add(secondMotionPart1);
            edges.add(secondMotionPart2);
            nodes.add(newEnd);
        } else {
            newEnd = second.getEndNode();
            blendEnd = second.getMotion();
        }

        //create Blend
        SkeletonInterpolator blendedMotion = blending.blend(blendStart, blendEnd, DEFAULT_BLENDING_FRAMES);
        Edge blended = new Edge(blendedMotion);
        blended.setBlend(true);

        newStart.addOutgoingEdge(blended);
        newEnd.addIncomingEdge(blended);

        edges.add(blended);
    }

    @Override
    public String toString() {
        String ret = "Edges: " + edges.size() + "\n";
        for (Edge edge : edges) {
            ret += edge + "\n";
        }
        return ret;

    }

    public IAlignment getAlign() {
        return align;
    }

    /**
     * Builder for MotionGraph.
     */
    public static class Builder {

        private IAlignment align;
        private IBlend blending;
        private IDistance metric;
        private ISplit split;
        private List<SkeletonInterpolator> motions = null;
        private Collection<Edge> edges;
        private Collection<Node> nodes;
        
        public Builder(Collection<Edge> edges, Collection<Node> nodes)
        {
            this.edges = edges;
            this.nodes = nodes;
        }
        
        public Builder(List<SkeletonInterpolator> motions) {
            this.motions = motions;
        }

        public MotionGraph getInstance() {
            this.align = align != null ? align : new Alignment();
            this.metric = metric != null ? metric : new JointAngles(align);
            this.blending = blending != null ? blending : new Blend(align);
            this.split = split != null ? split : new DefaultSplit();            
            if(motions==null)
            {
                return new MotionGraph(this.edges, this.nodes, this.align, this.metric, this.blending, this.split);
            }
            else
            {
                return new MotionGraph(this.motions, this.align, this.metric, this.blending, this.split);
            }
        }

        public Builder align(IAlignment align) {
            this.align = align;
            return this;
        }

        public Builder blending(IBlend blending) {
            this.blending = blending;
            return this;
        }

        public Builder metric(IDistance metric) {
            this.metric = metric;
            return this;
        }

        public Builder split(ISplit split) {
            this.split = split;
            return this;
        }

    }
}
