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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * A collection of methods that operate on {@link com.arsdigita.util.Graph
 * graphs}.
 *
 * @author Archit Shah (ashah@mit.edu)
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @version $Date: 2003/08/14 $
 * @since 2003-01-22
 **/
public class Graphs {
    private static final String INDENT = "    ";

    private Graphs() {}

    /**
     * Finds a path in <code>graph</code> from begin to end
     * 
     *
     * @returns list of edges representing the found path
     **/
    public static final List findPath(Graph graph, Object begin, Object end) {
        List path = new ArrayList(findPathRecurse(graph, begin, end, new HashSet()));
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
        Graph result = new GraphSet();
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

    /**
     * Pretty-prints the tree in a format patterned off of the <a
     * href="http://www.research.att.com/sw/tools/graphviz/refs.html">DOT
     * language</a>.
     *
     * @pre tree != null
     * @pre fmtr != null
     * @pre writer != null
     **/
    public static void printTree(Tree tree, GraphFormatter fmtr,
                                 PrintWriter writer) {

        Assert.assertNotNull(tree, "tree");
        Assert.assertNotNull(fmtr, "formatter");
        Assert.assertNotNull(writer, "writer");

        writer.println("digraph " + tree.getLabel() + " {");
        printTreeRecurse(tree, fmtr, writer);
        writer.println("}");
    }

    private static void printTreeRecurse(Tree tree, GraphFormatter fmtr,
                                         PrintWriter writer) {

        String root = fmtr.nodeName(tree.getRoot());
        for (Iterator ii=tree.getSubtrees().iterator(); ii.hasNext(); ) {
            Tree.EdgeTreePair pair = (Tree.EdgeTreePair) ii.next();
            String edge = fmtr.edge(pair.getEdge());
            String child = fmtr.nodeName(pair.getTree().getRoot());
            writer.print(INDENT + root + " -> " + child);
            if ( edge != null ) {
                writer.print("[label=\"" + edge + "\"]");
            }
            writer.println(";");
            printTreeRecurse(pair.getTree(), fmtr, writer);
        }
    }

    /**
     * Pretty-prints the graph.
     *
     * @see #printTree(Tree, GraphFormatter,  PrintWriter)
     * @pre graph != null
     * @pre fmtr != null
     * @pre writer != null
     **/
    public static void printGraph(Graph graph, GraphFormatter fmtr,
                                  PrintWriter writer) {

        Assert.assertNotNull(graph, "tree");
        Assert.assertNotNull(fmtr, "formatter");
        Assert.assertNotNull(writer, "writer");

        writer.println("digraph " + graph.getLabel() + " {");
        String graphAttrs = fmtr.graphAttributes(graph);
        if ( graphAttrs != null ) {
            writer.println(graphAttrs);
        }
        for (Iterator nodes=graph.getNodes().iterator(); nodes.hasNext(); ) {
            Object node = nodes.next();
            int nodeCount = graph.outgoingEdgeCount(node) +
                graph.incomingEdgeCount(node);

            String nodeName = fmtr.nodeName(node);
            String nodeAttrs = fmtr.nodeAttributes(node);

            if ( nodeCount==0 || nodeAttrs != null ) {
                writer.print(INDENT + nodeName);

                if ( nodeAttrs == null ) {
                    writer.println(";");
                } else {
                    writer.println(nodeAttrs + ";");
                }
            }

            if (graph.outgoingEdgeCount(node) == 0) {
                // we'll print this node when we print the outgoing edges of
                // some other node
                continue;
            }
            Iterator edges = graph.getOutgoingEdges(node).iterator();
            while (edges.hasNext()) {
                Graph.Edge edge = (Graph.Edge) edges.next();
                StringBuffer sb = new StringBuffer();
                sb.append(INDENT).append(fmtr.nodeName(edge.getTail()));
                sb.append(" -> ").append(fmtr.nodeName(edge.getHead()));
                if ( edge.getLabel() != null ) {
                    sb.append("[label=\"");
                    sb.append(fmtr.edge(edge.getLabel()));
                    sb.append("\"]");
                }
                sb.append(";");
                writer.println(sb.toString());
            }
        }
        writer.println("}");
    }

}
