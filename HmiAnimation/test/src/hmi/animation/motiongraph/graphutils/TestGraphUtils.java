package hmi.animation.motiongraph.graphutils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import hmi.animation.SkeletonInterpolator;
import hmi.animation.motiongraph.Edge;
import hmi.animation.motiongraph.MotionGraph;
import hmi.animation.motiongraph.Node;
import hmi.animation.motiongraph.graphutils.GraphUtils;
import hmi.animation.motiongraph.graphutils.GraphUtils.SCCDAG;
import hmi.animation.motiongraph.graphutils.GraphUtils.SCCDAGEdge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.hamcrest.Matchers;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class TestGraphUtils
{
    private Collection<Edge> edges = new ArrayList<>();
    private Collection<Node> nodes = new ArrayList<>();
    private SkeletonInterpolator interPol = new SkeletonInterpolator();

    private Edge constructEdge(Node n1, Node n2)
    {
        Edge e = new Edge(n1, n2, interPol);
        n1.addOutgoingEdge(e);
        n2.addIncomingEdge(e);
        return e;
    }

    @Test
    public void testGetConnected()
    {
        Node n1 = new Node();
        Node n2 = new Node();
        Node n3 = new Node();
        Node n4 = new Node();
        Edge e1 = constructEdge(n1, n2);
        Edge e2 = constructEdge(n1, n3);
        Edge e3 = constructEdge(n2, n3);

        nodes.add(n1);
        nodes.add(n2);
        nodes.add(n3);
        nodes.add(n4);
        edges.add(e1);
        edges.add(e2);
        edges.add(e3);
        MotionGraph mg = new MotionGraph.Builder(edges, nodes).getInstance();
        Map<Node, Integer> nodeMap = GraphUtils.getConnected(mg);

        System.out.println(nodeMap);
        assertEquals(nodeMap.get(n1), nodeMap.get(n2));
        assertEquals(nodeMap.get(n2), nodeMap.get(n3));
        assertNotEquals(nodeMap.get(n4), nodeMap.get(n1));
    }

    @Test
    public void testPost()
    {
        // n1->n2->n1
        // n2->n3->n4
        Node n1 = new Node();
        Node n2 = new Node();
        Node n3 = new Node();
        Node n4 = new Node();
        Edge e1 = constructEdge(n1, n2);
        Edge e2 = constructEdge(n2, n1);
        Edge e3 = constructEdge(n2, n3);
        Edge e4 = constructEdge(n3, n4);
        nodes.add(n1);
        nodes.add(n2);
        nodes.add(n3);
        nodes.add(n4);
        edges.add(e1);
        edges.add(e2);
        edges.add(e3);
        edges.add(e4);
        MotionGraph mg = new MotionGraph.Builder(edges, nodes).getInstance();

        Map<Node, Integer> post = GraphUtils.getPostNumbers(mg);
        assertThat(post.get(n1), greaterThan(post.get(n3)));
        assertThat(post.get(n2), greaterThan(post.get(n3)));
        assertThat(post.get(n3), greaterThan(post.get(n4)));
    }

    @Test
    public void testReverse()
    {
        // n1->n2->n1
        // n2->n3->n4
        Node n1 = new Node();
        Node n2 = new Node();
        Node n3 = new Node();
        Node n4 = new Node();
        Edge e1 = constructEdge(n1, n2);
        Edge e2 = constructEdge(n2, n1);
        Edge e3 = constructEdge(n2, n3);
        Edge e4 = constructEdge(n3, n4);
        nodes.add(n1);
        nodes.add(n2);
        nodes.add(n3);
        nodes.add(n4);
        edges.add(e1);
        edges.add(e2);
        edges.add(e3);
        edges.add(e4);
        MotionGraph mg = new MotionGraph.Builder(edges, nodes).getInstance();
        MotionGraph mgReverse = GraphUtils.reverse(mg);
        assertEquals(4, mgReverse.getNodes().size());
        assertEquals(4, mgReverse.getEdges().size());

        // n1<-n2<-n1
        // n2<-n3<-n4
        Node n1rev = mgReverse.getNode(n1.getId());
        Node n2rev = mgReverse.getNode(n2.getId());
        Node n3rev = mgReverse.getNode(n3.getId());
        Node n4rev = mgReverse.getNode(n4.getId());
        Edge e1rev = mgReverse.getEdge(e1.getId());
        Edge e2rev = mgReverse.getEdge(e2.getId());
        Edge e3rev = mgReverse.getEdge(e3.getId());
        Edge e4rev = mgReverse.getEdge(e4.getId());
        assertThat(n1rev.getIncomingEdges(), IsIterableContainingInAnyOrder.containsInAnyOrder(e1rev));
        assertThat(n1rev.getOutgoingEdges(), IsIterableContainingInAnyOrder.containsInAnyOrder(e2rev));
        assertThat(n2rev.getIncomingEdges(), IsIterableContainingInAnyOrder.containsInAnyOrder(e3rev, e2rev));
        assertThat(n2rev.getOutgoingEdges(), IsIterableContainingInAnyOrder.containsInAnyOrder(e1rev));
        assertThat(n3rev.getIncomingEdges(), IsIterableContainingInAnyOrder.containsInAnyOrder(e4rev));
        assertThat(n3rev.getOutgoingEdges(), IsIterableContainingInAnyOrder.containsInAnyOrder(e3rev));
        assertThat(n4rev.getIncomingEdges(), Matchers.<Edge> empty());
        assertThat(n4rev.getOutgoingEdges(), IsIterableContainingInAnyOrder.containsInAnyOrder(e4rev));

        assertEquals(n2rev, e1rev.getStartNode());
        assertEquals(n1rev, e1rev.getEndNode());
        assertEquals(n1rev, e2rev.getStartNode());
        assertEquals(n2rev, e2rev.getEndNode());
        assertEquals(n3rev, e3rev.getStartNode());
        assertEquals(n2rev, e3rev.getEndNode());
        assertEquals(n4rev, e4rev.getStartNode());
        assertEquals(n3rev, e4rev.getEndNode());
    }

    @Test
    public void testSCC()
    {
        // n1->n2->n1
        // n2->n3->n4
        Node n1 = new Node();
        Node n2 = new Node();
        Node n3 = new Node();
        Node n4 = new Node();
        Edge e1 = constructEdge(n1, n2);
        Edge e2 = constructEdge(n2, n1);
        Edge e3 = constructEdge(n2, n3);
        Edge e4 = constructEdge(n3, n4);
        nodes.add(n1);
        nodes.add(n2);
        nodes.add(n3);
        nodes.add(n4);
        edges.add(e1);
        edges.add(e2);
        edges.add(e3);
        edges.add(e4);
        MotionGraph mg = new MotionGraph.Builder(edges, nodes).getInstance();

        SCCDAG dag = GraphUtils.getStronglyConnectedComponents(mg);

        // resulting DAG:
        // [n1n2](3)->[n3](2)->[n4](1)
        assertEquals(dag.getInnerNodeMap().get(n1), dag.getInnerNodeMap().get(n2));
        assertThat(dag.getInnerNodeMap().get(n3), lessThan(dag.getInnerNodeMap().get(n2)));
        assertThat(dag.getInnerNodeMap().get(n4), lessThan(dag.getInnerNodeMap().get(n3)));
        assertEquals(3, dag.getNodes().size());
        assertThat(dag.getEdges(), IsIterableContainingInAnyOrder.containsInAnyOrder(new SCCDAGEdge(3, 2), new SCCDAGEdge(2, 1)));
    }

    @Test
    public void testSCC2()
    {
        // n1->n2->n1
        // n3->n4
        Node n1 = new Node();
        Node n2 = new Node();
        Node n3 = new Node();
        Node n4 = new Node();
        Edge e1 = constructEdge(n1, n2);
        Edge e2 = constructEdge(n2, n1);
        Edge e4 = constructEdge(n3, n4);
        nodes.add(n1);
        nodes.add(n2);
        nodes.add(n3);
        nodes.add(n4);
        edges.add(e1);
        edges.add(e2);
        edges.add(e4);
        MotionGraph mg = new MotionGraph.Builder(edges, nodes).getInstance();

        SCCDAG dag = GraphUtils.getStronglyConnectedComponents(mg);
        // resulting DAG (example):
        // [n1n2](3) [n3](2)->[n4](1)
        assertEquals(3, dag.getNodes().size());
        assertEquals(dag.getInnerNodeMap().get(n1), dag.getInnerNodeMap().get(n2));
        assertThat(dag.getInnerNodeMap().get(n4), lessThan(dag.getInnerNodeMap().get(n3)));
        assertThat(
                dag.getEdges(),
                IsIterableContainingInAnyOrder.containsInAnyOrder(new SCCDAGEdge(dag.getInnerNodeMap().get(n3), dag.getInnerNodeMap().get(
                        n4))));
    }

    @Test
    public void testSCC3()
    {
        Node a = new Node();
        Node b = new Node();
        Node c = new Node();
        Node d = new Node();
        Node e = new Node();
        Node f = new Node();
        Node g = new Node();
        Node h = new Node();
        Edge ab = constructEdge(a, b);
        Edge bc = constructEdge(b, c);
        Edge cd = constructEdge(c, d);
        Edge db = constructEdge(d, b);
        Edge be = constructEdge(b, e);
        Edge ed = constructEdge(e, d);
        Edge af = constructEdge(a, f);
        Edge fe = constructEdge(f, e);
        Edge eg = constructEdge(e, g);
        Edge fg = constructEdge(f, g);
        Edge gf = constructEdge(g, f);
        Edge hg = constructEdge(h, g);
        nodes = ImmutableList.of(a, b, c, d, e, f, g, h);
        edges = ImmutableList.of(ab, bc, cd, db, be, ed, af, fe, eg, fg, gf, hg);
        MotionGraph mg = new MotionGraph.Builder(edges, nodes).getInstance();

        SCCDAG dag = GraphUtils.getStronglyConnectedComponents(mg);
        assertEquals(3, dag.getNodes().size());
        assertEquals(dag.getInnerNodeMap().get(b), dag.getInnerNodeMap().get(c));
        assertEquals(dag.getInnerNodeMap().get(b), dag.getInnerNodeMap().get(d));
        assertEquals(dag.getInnerNodeMap().get(b), dag.getInnerNodeMap().get(e));
        assertEquals(dag.getInnerNodeMap().get(b), dag.getInnerNodeMap().get(f));
        assertEquals(dag.getInnerNodeMap().get(b), dag.getInnerNodeMap().get(g));
        assertThat(dag.getInnerNodeMap().get(a), greaterThan(dag.getInnerNodeMap().get(b)));
        assertThat(dag.getInnerNodeMap().get(h), greaterThan(dag.getInnerNodeMap().get(b)));
        System.out.println(dag.getEdges());
        assertThat(dag.getEdges(), IsIterableContainingInAnyOrder.containsInAnyOrder(new SCCDAGEdge(dag.getInnerNodeMap().get(a), dag
                .getInnerNodeMap().get(b)), new SCCDAGEdge(dag.getInnerNodeMap().get(h), dag.getInnerNodeMap().get(b))));
    }
}
