/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @version $Date: 2003/08/01 $
 * @since 2003-01-22
 **/
public class GraphSetTest extends TestCase {
    private static final String NODE_A = "A";
    private static final String NODE_B = "B";
    private static final String NODE_C = "C";
    private static final String NODE_D = "D";

    public void testAddNode() {
        Graph graph = new GraphSet();
        graph.addNode(NODE_A);
        assertTrue("node count=1", graph.nodeCount() == 1);
        graph.addNode(NODE_B);
        assertTrue("node count=2", graph.nodeCount() == 2);
        graph.addNode(NODE_B);
        assertTrue("after adding twice, node count=2", graph.nodeCount() == 2);

        assertTrue("A has no outgoing edges",
                   graph.getOutgoingEdges(NODE_A).size() == 0);
        assertTrue("A has no incoming edges", 
                   graph.getIncomingEdges(NODE_A).size() == 0);
    }

    public void testAddEdge() {
        Graph graph = new GraphSet();
        final String label = "a to b";
        graph.addEdge(NODE_A, NODE_B, label);
        countEdges(graph);
        graph.addEdge(NODE_A, NODE_B, label);
        graph.addNode(NODE_A);
        countEdges(graph);
        graph.addNode(NODE_B);
        countEdges(graph);
    }

    private static void countEdges(Graph graph) {
        assertTrue("node count=2", graph.nodeCount() == 2);
        assertTrue("A's expected out-edge count: 1. Actual count=" +
                   graph.outgoingEdgeCount(NODE_A),
                   graph.outgoingEdgeCount(NODE_A) == 1);
        assertTrue("A has zero incoming edge count",
                   graph.incomingEdgeCount(NODE_A) == 0);
        assertTrue("B has zero outgoing edge count",
                   graph.outgoingEdgeCount(NODE_B) == 0);
        assertTrue("B has one incoming edge count",
                   graph.incomingEdgeCount(NODE_B) == 1);
    }

    public void testCopy() {
        Graph graphX = new GraphSet();
        graphX.setLabel("X");
        graphX.addEdge(NODE_A, NODE_B, "a -> b");
        graphX.addEdge(NODE_A, NODE_C, "a -> c");
        graphX.addEdge(NODE_B, NODE_C, "b -> c");

        Graph graphY = graphX.copy();
        graphY.setLabel("Y");

        String diff = getPartialDiff(graphX, graphY);
        assertNull("diff(X, Y) = " + diff, diff);
        graphX.addEdge(NODE_C, NODE_D, "a -> d");
        assertNotNull("X and Y should be different",
                      getPartialDiff(graphX, graphY));
    }

    public void testRemoveAll() {
        Graph graph = new GraphSet();
        graph.setLabel("X");
        graph.addEdge(NODE_A, NODE_B, "a -> b");
        graph.addEdge(NODE_A, NODE_C, "a -> c");
        graph.addEdge(NODE_B, NODE_C, "b -> c");

        List edges = graph.getOutgoingEdges(NODE_A);
        edges.addAll(graph.getIncomingEdges(NODE_C));
        graph.removeAll();

        assertEquals("node count", 0, graph.nodeCount());

        for (Iterator ii=edges.iterator(); ii.hasNext(); ) {
            Graph.Edge edge = (Graph.Edge) ii.next();
            assertTrue("no such edge", !graph.hasEdge(edge));
        }
    }

    public void testRemove() {
        Graph graph = new GraphSet();
        graph.setLabel("X");
        graph.addEdge(NODE_A, NODE_B, "a -> b");
        graph.addEdge(NODE_A, NODE_C, "a -> c");
        graph.addEdge(NODE_B, NODE_C, "b -> c");

        graph.addEdge(NODE_D, NODE_C, "d -> c");
        graph.addEdge(NODE_D, NODE_A, "d -> a");
        graph.addEdge(NODE_B, NODE_D, "b -> d");
        graph.addEdge(NODE_D, NODE_D, "d -> d");

        graph.removeNode(NODE_D);

        for (Iterator it = graph.getNodes().iterator(); it.hasNext(); ) {
            Object node = it.next();
            if (node.equals(NODE_D)) {
                fail("node d should have been removed");
            }
            for (Iterator it2 = graph.getOutgoingEdges(node).iterator();
                 it2.hasNext(); ) {
                Graph.Edge edge = (Graph.Edge) it2.next();
                if (edge.getTail().equals(NODE_D)
                    || edge.getHead().equals(NODE_D)) {
                    fail("node d should have been removed");
                }
            }
            for (Iterator it2 = graph.getIncomingEdges(node).iterator();
                 it2.hasNext(); ) {
                Graph.Edge edge = (Graph.Edge) it2.next();
                if (edge.getTail().equals(NODE_D)
                    || edge.getHead().equals(NODE_D)) {
                    fail("node d should have been removed");
                }
            }
        }
    }

    public void testRemoveEdge() {
        Graph graph = new GraphSet();
        graph.setLabel("X");
        graph.addEdge(NODE_A, NODE_B, "a -> b");
        graph.addEdge(NODE_A, NODE_C, "a -> c");
        graph.addEdge(NODE_B, NODE_C, "b -> c");
        graph.addEdge(NODE_D, NODE_C, "d -> c");

        GraphEdge ge = new GraphEdge(NODE_D, NODE_D, "d -> d");
        GraphEdge ge2 = new GraphEdge(NODE_B, NODE_D, "b -> d");

        graph.addEdge(ge);
        graph.addEdge(ge2);
        graph.removeEdge(ge);
        graph.removeEdge(ge2);

        for (Iterator it = graph.getNodes().iterator(); it.hasNext(); ) {
            Object node = it.next();
            for (Iterator it2 = graph.getOutgoingEdges(node).iterator();
                 it2.hasNext(); ) {
                Graph.Edge edge = (Graph.Edge) it2.next();
                if (edge.equals(ge)) {
                    fail("edge d -> d should have been removed");
                } else if (edge.equals(ge2)) {
                    fail("edge b -> d should have been removed");
                }
            }
            for (Iterator it2 = graph.getIncomingEdges(node).iterator();
                 it2.hasNext(); ) {
                Graph.Edge edge = (Graph.Edge) it2.next();
                if (edge.equals(ge)) {
                    fail("edge d -> d should have been removed");
                } else if (edge.equals(ge2)) {
                    fail("edge b -> d should have been removed");
                }
            }
        }
    }

    private static String getPartialDiff(Graph xx, Graph yy) {
        String diff = diffNodesOneWay(xx, yy);
        if (diff != null ) {
            return diff;
        }

        diff = diffNodesOneWay(yy, xx);
        if ( diff != null ) {
            return diff;
        }

        diff = diffEdgesOneWay(xx, yy);
        if ( diff != null ) {
            return diff;
        }

        diff = diffEdgesOneWay(yy, xx);
        if ( diff != null ) {
            return diff;
        }

        return null;
    }

    private static String diffNodesOneWay(Graph xx, Graph yy) {
        for (Iterator ii=xx.getNodes().iterator(); ii.hasNext(); ) {
            Object node = ii.next();
            if ( ! yy.hasNode(node) ) {
                return xx.getLabel() + " has node " + nodeToString(node) +
                    ", but " + yy.getLabel() + " does not";
            }
        }
        return null;
    }

    /**
     * @pre nodeSuperset(xx, yy)
     **/
    private static String diffEdgesOneWay(Graph xx, Graph yy) {
        for (Iterator ii=xx.getNodes().iterator(); ii.hasNext(); ) {
            for (Iterator jj=xx.getOutgoingEdges(ii.next()).iterator(); jj.hasNext(); ) {
                Graph.Edge edge = (Graph.Edge) jj.next();
                if ( ! yy.hasEdge(edge) ) {
                    return xx.getLabel() + " has the edge '" + edge +
                        "', but " + yy.getLabel() + " does not.";
                }
            }
        }
        return null;
    }

    private static String nodeToString(Object node) {
        return node == null ? "null" : node.toString();
    }
}
