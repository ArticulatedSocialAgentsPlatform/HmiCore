package hmi.animation.motiongraph;

import hmi.animation.SkeletonInterpolator;
import hmi.animation.VJoint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public void addSkeletonInterpolator(SkeletonInterpolator ski)
    {
        MGNode outgoing = new MGNode();
        MGEdge newEdge = new MGEdge(ski, outgoing);
        MGNode incoming = new MGNode();
        incoming.addEdge(newEdge);
        
        List<MGEdge> removeEdges = new ArrayList<MGEdge>();
        
        //need something different...
        for (int i = 0; i < ski.size(); i++)
        {
            float config[] = ski.getConfig(i);
            for(MGEdge edge:edges)
            {
                SkeletonInterpolator ski2 = edge.getMotion();
                for (int j = 0; j < ski.size(); j++)
                {
                    //compare configs
                    //if similar, insert new node+edges, get rid of existing edge                 
                }
            }
        }
        edges.removeAll(removeEdges);
    }

    public void insertTransition(MGEdge edgeIn, MGEdge edgeOut, int iIn, int iOut)
    {
        MGNode n1 = new MGNode();
        MGNode n2 = new MGNode();
        
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
