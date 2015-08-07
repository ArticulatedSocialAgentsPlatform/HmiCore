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
