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

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by Zukie on 15/06/15.
 */
public class Node {

    public static int nodeId = 0;
    private int id;
    private List<Edge> incomingEdges = new LinkedList<Edge>();
    private List<Edge> outgoingEdges = new LinkedList<Edge>();



    /**
     * Constructor, creates new Node with given incoming and outgoing edges.
     * Both can be Null if not yet created.
     *
     * @param incomingEdge
     * @param outgoingEdge
     */
    public Node(Edge incomingEdge, Edge outgoingEdge) {
        this.id = nodeId++;

        if (incomingEdge != null) {
            this.incomingEdges.add(incomingEdge);
            incomingEdge.setEndNode(this);
        }
        if (outgoingEdge != null) {
            this.outgoingEdges.add(outgoingEdge);
            outgoingEdge.setStartNode(this);
        }



    }
    
    public Node(){this(null,null);}

    /**
     * Adds new incoming Edge to Node and sets Node as it's ending point.
     * @param incoming edge
     */
    public void addIncomingEdge(Edge incoming){
        if (!this.getIncomingEdges().contains(incoming)) {
            this.incomingEdges.add(incoming);
            incoming.setEndNode(this);
        }
    }

    /**
     * Adds new outgoing edge to node and sets node as starting point.
     * @param outgoing edge
     */
    public void addOutgoingEdge(Edge outgoing){
        if (!this.getOutgoingEdges().contains(outgoing)) {
            this.outgoingEdges.add(outgoing);
            outgoing.setStartNode(this);
        }
    }

    /**
     * Checks if Node has outgoing edges.
     * @return false, if Node does not have any outgoing edges. True if it does.
     */
    public boolean hasNext() {

        if (this.outgoingEdges.isEmpty()) {
            return false;
        }

        return true;
    }

    /**
     * Prints Node's id together with incoming and outgoing edges.
     * @return id + incoming and outgoing edges
     */
    @Override
    public String toString() {
        return "NodeId: "+this.getId()+"; In: "
                +this.getIncomingEdges()+"; Out: "+this.getOutgoingEdges();
    }

    //<editor-fold desc="Getter and Setter">


    /**
     * @return random Node connected to this node.
     */
    public Edge getNext() {
        Random r = new Random();
        int next = r.nextInt(this.outgoingEdges.size());

        return this.outgoingEdges.get(next);
    }

    public List<Edge> getIncomingEdges() {
        return incomingEdges;
    }

    public void setIncomingEdges(List<Edge> incomingEdges) {
        this.incomingEdges = incomingEdges;
    }

    public List<Edge> getOutgoingEdges() {
        return outgoingEdges;
    }

    public void setOutgoingEdges(List<Edge> outgoingEdges) {
        this.outgoingEdges = outgoingEdges;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    //</editor-fold>


    
}
