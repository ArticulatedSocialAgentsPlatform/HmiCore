package hmi.animation.motiongraph.xml;

import hmi.animation.motiongraph.Edge;
import hmi.animation.motiongraph.MotionGraph;
import hmi.animation.motiongraph.Node;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLScanException;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

/**
 * Load/store a motiongraph
 * @author hvanwelbergen
 *
 */
public class MotionGraphXML extends XMLStructureAdapter
{
    @Getter
    private MotionGraph motionGraph;

    public MotionGraphXML()
    {

    }

    public MotionGraphXML(MotionGraph mg)
    {
        this.motionGraph = mg;
    }

    @Override
    public void decodeContent(XMLTokenizer tokenizer) throws IOException
    {
        Map<Integer, Node> nodeMap = new HashMap<>();
        List<Edge> edges = new ArrayList<Edge>();
        while (tokenizer.atSTag())
        {
            String tag = tokenizer.getTagName();
            if (tag.equals(EdgeXML.xmlTag()))
            {
                EdgeXML edgeXML = new EdgeXML();
                edgeXML.readXML(tokenizer);
                Edge e = edgeXML.getEdge();
                Node n = getNode(nodeMap, edgeXML.getStartNodeId());
                e.setStartNode(n);
                n.addOutgoingEdge(e);
                n = getNode(nodeMap, edgeXML.getEndNodeId());
                e.setEndNode(n);
                n.addIncomingEdge(e);
                e.setId(edgeXML.getId());
                edges.add(e);
            }
            else
            {
                throw new XMLScanException("unknown tag " + tag);
            }
        }
        motionGraph = new MotionGraph.Builder(edges, nodeMap.values()).getInstance();
    }

    private Node getNode(Map<Integer, Node> nodeMap, int nodeId)
    {
        Node n;
        if (nodeMap.containsKey(nodeId))
        {
            n = nodeMap.get(nodeId);
        }
        else
        {
            n = new Node();
            n.setId(nodeId);
            nodeMap.put(nodeId, n);
        }
        return n;
    }

    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt)
    {
        for (Edge e : motionGraph.getEdges())
        {
            EdgeXML edgeXML = new EdgeXML(e);
            edgeXML.appendXML(buf, fmt);
        }
        return super.appendContent(buf, fmt);
    }

    private static final String XMLTAG = "motiongraph";

    /**
     * The XML Stag for XML encoding -- use this static method when you want to see if a given
     * String equals the xml tag for this class
     */
    public static String xmlTag()
    {
        return XMLTAG;
    }

    /**
     * The XML Stag for XML encoding -- use this method to find out the run-time xml tag of an
     * object
     */
    @Override
    public String getXMLTag()
    {
        return XMLTAG;
    }

    /*
    public static void main(String args[]) throws IOException
    {
        long time = System.currentTimeMillis();
        List<SkeletonInterpolator> motions = LoadMotion.loadMotion(new String[] { "idle_0_10.xml", "idle_10_20.xml", "idle_20_30.xml",
                "idle_30_40.xml", "idle_40_50.xml", "idle_50_60.xml", "idle_60_70.xml", "1_From500.xml", "3_0-530.xml", "5.xml",
                "3_1536-2517.xml", "4.xml", "6.xml", "2_0-867.xml", "2_1998-2778.xml", "2_867-1998.xml", "3_530-1536.xml", });

        MotionGraphXML mgXML = new MotionGraphXML(new MotionGraph.Builder(motions).align(new NopAlignment()).getInstance());
        System.out.println("motiongraph construction duration: " + (System.currentTimeMillis() - time)+" ms ");        
        mgXML.writeXML(new File("motiongraph_notpruned.xml"));
        GraphUtils.pruneSinkSCCs(mgXML.getMotionGraph(), 10);
        mgXML.writeXML(new File("motiongraph.xml"));

        time = System.currentTimeMillis();
        MotionGraphXML mgXMLin = new MotionGraphXML();
        mgXMLin.readXML(new File("motiongraph.xml"));
        System.out.println("motiongraph parse duration: " + (System.currentTimeMillis() - time)+" ms ");
    }
    */
}
