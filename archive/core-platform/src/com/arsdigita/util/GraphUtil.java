package com.arsdigita.util;

import com.arsdigita.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class GraphUtil {
    private GraphUtil() { } // prevent construction

    /**
     * Finds a path in g from begin to end
     * 
     *
     * @returns list of edges representing the found path
     **/
    public static final List findPath(Graph g, Object begin, Object end) {
        List path = new ArrayList(findPathRecurse(g, begin, end, new HashSet()));
        Collections.reverse(path);
        return path;
    }

    private static final Stack findPathRecurse(
        Graph g, Object start, Object finish, Set searched) {

        Iterator it = g.getOutgoingEdges(start).iterator();

        searched.add(start);

        while (it.hasNext()) {
            Graph.Edge e = (Graph.Edge) it.next();

            if (e.getHead().equals(finish)) {
                Stack s = new Stack();
                s.push(e);
                return s;
            }
        }

        it = g.getOutgoingEdges(start).iterator();
        while (it.hasNext()) {
            Graph.Edge e = (Graph.Edge) it.next();
            if (!searched.contains(e.getHead())) {
                Stack answer = findPathRecurse(g, e.getHead(), finish, searched);
                if (answer != null) {
                    answer.push(e);
                    return answer;
                }
            }
        }

        return null;
    }

    /**
     * @param edgePath a list of edges such as the one returned by
     * {@link #findPath(Graph, Object, Object)}.
     * @return the same path represented as a list of nodes rather than edges.
     **/
    public static final List edgePathToNodePath(List edgePath) {
        List path = new ArrayList();
        Graph.Edge lastEdge = null;
        for (Iterator edges = edgePath.iterator(); edges.hasNext(); ) {
            Graph.Edge edge = (Graph.Edge) edges.next();
            if ( lastEdge != null ) {
                Assert.assertEquals(lastEdge.getHead(), edge.getTail(),
                                    "lastEdge.getHead()", "edge.getTail()");
            }
            path.add(edge.getTail());
            lastEdge = edge;
        }
        if ( lastEdge != null ) {
            path.add(lastEdge.getHead());
        }
        return path;
    }

    private static String objToString(Object obj) {
        return obj == null ? "null" : obj.toString();
    }

    /**
     * @return nodes reachable from <code>start</code>, including the
     * <code>start</code> node itself.
     *
     * @pre graph.hasNode(start)
     **/
    public static Graph nodesReachableFrom(Graph graph, Object start) {
        Assert.assertTrue(graph.hasNode(start));
        Graph result = new Graph();
        result.addNode(start);
        Set processedTails = new HashSet();
        nodesReachableRecurse(graph, start, processedTails, result);
        return result;
    }

    private static void nodesReachableRecurse(Graph gg, Object currentNode,
                                              Set processedTails,
                                              Graph accumulator) {

        processedTails.add(currentNode);

        for (Iterator edges=gg.getOutgoingEdges(currentNode).iterator(); edges.hasNext(); ) {
            Graph.Edge edge = (Graph.Edge) edges.next();
            if ( processedTails.contains(edge.getHead()) ) {
                continue;
            }
            accumulator.addEdge(edge);
            nodesReachableRecurse
                (gg, edge.getHead(), processedTails, accumulator);
        }
    }

    /**
     * Returns a list of nodes in <code>gg</code> that have no outgoing edges.
     **/
    public static List getSinkNodes(Graph gg) {
        List result = new ArrayList();
        for (Iterator nodes = gg.getNodes().iterator(); nodes.hasNext(); ) {
            Object node = nodes.next();
            if ( gg.getOutgoingEdges(node).size() == 0 ) {
                result.add(node);
            }
        }
        return result;
    }
}
