package hmi.animation.motiongraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import lombok.Getter;

public class MGNode
{
    @Getter
    private List<MGEdge> outgoingEdges = new ArrayList<MGEdge>();
    private Random rand = new Random(System.currentTimeMillis());
    
    public void addEdge(MGEdge edge)
    {
        outgoingEdges.add(edge);
    }

    public void removeEdge(MGEdge remove)
    {
        outgoingEdges.remove(remove);
    }

    public void removeEdges(Collection<MGEdge> removeEdges)
    {
        outgoingEdges.removeAll(removeEdges);
    }

    public MGEdge randomEdge()
    {
        int index = (int) rand.nextInt(outgoingEdges.size());
        return outgoingEdges.get(index);
    }
}
