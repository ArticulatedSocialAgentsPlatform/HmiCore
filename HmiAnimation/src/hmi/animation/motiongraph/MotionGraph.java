package hmi.animation.motiongraph;

import hmi.animation.ConfigList;
import hmi.animation.SkeletonInterpolator;
import hmi.animation.VJoint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import lombok.Data;

import com.google.common.collect.ImmutableList;

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
    private final TransitionChecker transitionChecker;
    private final Random rand = new Random(System.currentTimeMillis());
    
    @Data
    class Split
    {
        final MGEdge edge;
        final int splitPoint;
    }
    
    public List<MGNode> getNodes()
    {
        return ImmutableList.copyOf(nodes);
    }
    
    public List<MGEdge> getEdges()
    {
        return ImmutableList.copyOf(edges);
    }
    
    public MotionGraph(TransitionChecker tc)
    {
        transitionChecker = tc;
    }

    public void prune()
    {
        boolean pruneDone = false;
        
        while(!pruneDone)
        {
            pruneDone = true;
            Set<MGNode>removeNodes = new HashSet<MGNode>();
            for(MGNode node:nodes)
            {
                if(node.getOutgoingEdges().isEmpty())
                {
                    pruneDone = false;
                    removeNodes.add(node);
                }
            }
            removeNodes(removeNodes);
        }
    }
    
    private void removeNodes(Set<MGNode> removeNodes)
    {
        List<MGEdge>removeEdges = new ArrayList<MGEdge>();
        nodes.removeAll(removeNodes);
        for(MGEdge edge:edges)
        {
            if(removeNodes.contains(edge.getIncomingNode())||removeNodes.contains(edge.getOutgoingNode()))
            {
                removeEdges.add(edge);
            }
        }
        edges.removeAll(removeEdges);
        for(MGNode node:nodes)
        {
            node.removeEdges(removeEdges);
        }
    }
    
    private void connectEdge(MGEdge newEdge, int startFrame)
    {
        System.out.println("ConnectEdge");
        int minFrameSize = 20;
        for (int i = startFrame; i < newEdge.getMotion().size() - minFrameSize; i += minFrameSize)
        {
            List<Split> splitsTo = new ArrayList<Split>();
            List<Split> splitsFrom = new ArrayList<Split>();
            for (MGEdge edge : edges)
            {
                SkeletonInterpolator ski2 = edge.getMotion();
                for (int j = 0; j < ski2.size() - minFrameSize; j += minFrameSize)
                {
                    if(edge!=newEdge || Math.abs(i-j)>minFrameSize)
                    {
                        if (transitionChecker.allowTransition(newEdge.getMotion(), ski2, i, j))
                        {
                            System.out.println("Adding split to at "+i+"," + j);
                            splitsTo.add(new Split(edge, j));
                        }
                        if (transitionChecker.allowTransition(ski2, newEdge.getMotion(), j, i))
                        {
                            System.out.println("Adding split from at "+i+","+j);
                            splitsFrom.add(new Split(edge, j));
                        }
                    }
                }
            }

            if (!splitsTo.isEmpty() || !splitsFrom.isEmpty())
            {
                MGNode node = splitEdge(newEdge, i);
                newEdge = node.getOutgoingEdges().get(0);
                for (Split split : splitsTo)
                {
                    insertTransition(node, split.getEdge(), split.getSplitPoint());
                }
                for (Split split : splitsFrom)
                {
                    insertTransition(split.getEdge(), split.getSplitPoint(), node);
                }
                connectEdge(newEdge, minFrameSize);
                return;
            }
        }
    }
    
    public void addSkeletonInterpolator(SkeletonInterpolator ski)
    {
        MGNode outgoing = new MGNode();
        MGNode incoming = new MGNode();
        MGEdge newEdge = new MGEdge(ski, incoming, outgoing);
        
        edges.add(newEdge);
        nodes.add(outgoing);
        nodes.add(incoming);

        connectEdge(newEdge, 0);
    }

    public MGNode splitEdge(MGEdge edge, int frame)
    {
        if(frame==0)
        {
            return edge.getIncomingNode();
        }
        if(frame==edge.getMotion().size()-1)
        {
            return edge.getOutgoingNode();
        }
        SkeletonInterpolator ski1 = edge.getMotion().subSkeletonInterpolator(0, frame+1);
        SkeletonInterpolator ski2 = edge.getMotion().subSkeletonInterpolator(frame);
        MGNode node = new MGNode();
        MGEdge edge1 = new MGEdge(ski1, edge.getIncomingNode(), node);
        MGEdge edge2 = new MGEdge(ski2, node, edge.getOutgoingNode());
        
        edges.add(edge1);
        edges.add(edge2);
        nodes.add(node);
        
        edges.remove(edge);
        for(MGNode n:nodes)
        {
            n.removeEdge(edge);
        }
        
        return node;
    }

    private SkeletonInterpolator createTransition(float startConfig[], float endConfig[], String[] parts, String configType)
    {
        ConfigList configs = new ConfigList(startConfig.length);
        configs.addConfig(0, startConfig);
        configs.addConfig(0.4, endConfig);
        return new SkeletonInterpolator(parts, configs, configType);
    }

    private SkeletonInterpolator createTransition(MGNode start, MGNode end)
    {
        float startConfig[] = start.getOutgoingEdges().get(0).getMotion().getConfig(0);
        float endConfig[] = end.getOutgoingEdges().get(0).getMotion().getConfig(0);
        String[] parts = start.getOutgoingEdges().get(0).getMotion().getPartIds();
        String configType = start.getOutgoingEdges().get(0).getMotion().getConfigType();
        return createTransition(startConfig,endConfig,parts,configType);
    }

    // /Transition from start to edge:iOut, splits edge
    public void insertTransition(MGNode start, MGEdge edge, int iOut)
    {
        MGNode end = splitEdge(edge, iOut);
        SkeletonInterpolator skiTrans = createTransition(start, end);
        MGEdge transition = new MGEdge(skiTrans, start, end);
        edges.add(transition);
    }

    // /Transition from edge:iOut, to end, splits edge
    public void insertTransition(MGEdge edge, int iIn, MGNode end)
    {
        MGNode start = splitEdge(edge, iIn);
        SkeletonInterpolator skiTrans = createTransition(start, end);
        MGEdge transition = new MGEdge(skiTrans, start, end);
        edges.add(transition);
    }

    public void randomStart(double time)
    {
        int index = (int) Math.round(rand.nextDouble() * (nodes.size() - 1));
        MGNode startNode = nodes.get(index);
        changeEdge(time, startNode.randomEdge());
    }

    private void changeEdge(double time, MGEdge edge)
    {
        edgeStartTime = time;
        currentEdge = edge;
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
            //System.out.println("Change edge!");
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
