package hmi.animation.motiongraph;

import hmi.animation.SkeletonInterpolator;
import hmi.animation.VJoint;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * Implements a motion graph
 * @author herwinvw
 *
 */
public class MotionGraph
{
    private List<MGNode> nodes = new ArrayList<MGNode>();
    private List<MGEdge> edges = new ArrayList<MGEdge>();
    private MGEdge currentEdge;
    private double edgeStartTime;
    private double edgeEndTime;

    @Data
    class Split
    {
        final MGEdge edge;
        final int splitPoint;
    }
    
    public void addSkeletonInterpolator(SkeletonInterpolator ski)
    {
        MGNode outgoing = new MGNode();
        MGNode incoming = new MGNode();
        MGEdge newEdge = new MGEdge(ski, incoming, outgoing);
        edges.add(newEdge);
        nodes.add(outgoing);
        nodes.add(incoming);
        
        int minFrameSize = 10;
        int iOffset = 0;
        for (int i = 0; i < ski.size()-minFrameSize; i+=minFrameSize)
        {
            List<Split> splitsTo = new ArrayList<Split>();
            List<Split> splitsFrom = new ArrayList<Split>();
            for(MGEdge edge:edges)
            {
                SkeletonInterpolator ski2 = edge.getMotion();
                for (int j = 0; j < ski.size()-minFrameSize; j+=minFrameSize)
                {
                    //gather similar configs, add to splits                                     
                }
            }
            
            if(!splitsTo.isEmpty() || splitsFrom.isEmpty())
            {
                MGNode node = splitEdge(newEdge, i-iOffset);
                iOffset = i;
                newEdge = node.getOutgoingEdges().get(0);
                for(Split split:splitsTo)
                {
                    insertTransition(node, split.getEdge(), split.getSplitPoint());
                }
                for(Split split:splitsTo)
                {
                    insertTransition(split.getEdge(), split.getSplitPoint(), node);
                }
            }
        }        
    }

    public MGNode splitEdge(MGEdge edge, int frame)
    {
        SkeletonInterpolator ski1 = edge.getMotion().subSkeletonInterpolator(0, frame);
        SkeletonInterpolator ski2 = edge.getMotion().subSkeletonInterpolator(frame);
        MGNode node = new MGNode();
        MGEdge edge1 = new MGEdge(ski1, edge.getIncomingNode(), node);
        MGEdge edge2 = new MGEdge(ski2, node, edge.getOutgoingNode());
        edges.remove(edge);
        edges.add(edge1);
        edges.add(edge2);
        nodes.add(node);
        return node;
    }
    
    
    ///Transition from start to edge:iOut, splits edge
    public void insertTransition(MGNode start, MGEdge edge, int iOut)
    {
        MGNode end = splitEdge(edge, iOut);
        SkeletonInterpolator skiTrans = new SkeletonInterpolator(); //TODO: fill this
        MGEdge transition = new MGEdge(skiTrans, start, end);
        edges.add(transition);
    }
    
    ///Transition from edge:iOut, to end, splits edge
    public void insertTransition(MGEdge edge, int iIn, MGNode end)
    {
        MGNode start = splitEdge(edge, iIn);
        SkeletonInterpolator skiTrans = new SkeletonInterpolator(); //TODO: fill this
        MGEdge transition = new MGEdge(skiTrans, start, end);
        edges.add(transition);
    }
    
    public void randomStart(double time)
    {
        int index = (int) Math.round(Math.random() * (nodes.size() - 1));
        MGNode startNode = nodes.get(index);
        changeEdge(time, startNode.randomEdge());
    }

    private void changeEdge(double time, MGEdge edge)
    {
        edgeStartTime = time;
        edgeEndTime = time + currentEdge.getDuration();
    }

    public void randomWalk(double time)
    {
        if (time < edgeEndTime)
        {
            currentEdge.play(time - edgeStartTime);
        }
        else
        {
            changeEdge(time, currentEdge.getOutgoingNode().randomEdge());
        }
    }

    public void setTarget(VJoint human)
    {
        for (MGEdge edge : edges)
        {
            edge.setTarget(human);
        }
    }
}
