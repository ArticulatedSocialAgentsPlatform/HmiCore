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

/**
 * Created by Zukie on 15/06/15.
 */
public class Edge {

    private Node startNode;
    private Node endNode;
    public static int edgeId = 0;
    private int id;
    public int played=0;

    /**
     * specifies if motion is an original or a blended motion.
     */
    private boolean isBlend;
    private SkeletonInterpolator motion;

    /**
     * Constructor that is to be used if start and end points already exist.
     * @param startNode starting point of the edge
     * @param endNode ending point of the edge
     * @param motion motion that is represented by this edge
     */
    public Edge(Node startNode, Node endNode, SkeletonInterpolator motion) {
        this.id = edgeId++;
        this.startNode = startNode;
        this.endNode = endNode;
        this.isBlend = false;
        this.motion = motion;

    }

    /**
     * Constructor that is to be used in case you generate the motion
     * before adding start and end points.
     * @param motion motion that is represented by this motion
     */
    public Edge(SkeletonInterpolator motion) {
        this.id = edgeId++;
        this.motion = motion;
        this.isBlend = false;
    }

    public String toString() {
        double durationInt = motion.getEndTime() - motion.getStartTime();

        String toString = new String("Edge Id: " + this.id +
                ", StartTime: " + motion.getStartTime() + ", Duration: " + durationInt +
                ", StartId: " + startNode.getId() + ", EndId: " + endNode.getId());

        return toString;
    }

    //<editor-fold desc="Getter and Setter">
    public Node getStartNode() {
        return startNode;
    }

    public void setStartNode(Node startNode) {
        this.startNode = startNode;
        startNode.addOutgoingEdge(this);
    }

    public Node getEndNode() {
        return endNode;
    }

    public void setEndNode(Node endNode) {
        this.endNode = endNode;
        endNode.addIncomingEdge(this);
    }

    public boolean isBlend() {
        return isBlend;
    }

    public void setBlend(boolean isBlend) {
        this.isBlend = isBlend;
    }

    public SkeletonInterpolator getMotion() {
        return motion;
    }

    public void setMotion(SkeletonInterpolator motion) {
        this.motion = motion;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIsBlend(boolean isBlend) {
        this.isBlend = isBlend;
    }
    //</editor-fold>
}
