package hmi.animation.motiongraph;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class MGNode
{
    @Getter
    private List<MGEdge> outgoingEdges = new ArrayList<MGEdge>();
    
    public void addEdge(MGEdge edge)
    {
        outgoingEdges.add(edge);
    }
    
    public MGEdge randomEdge()
    {
        int index = (int)Math.round(Math.random()*(outgoingEdges.size()-1));
        return outgoingEdges.get(index);        
    }
}
