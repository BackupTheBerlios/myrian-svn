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

import java.util.List;

/**
 * The graph class allows you to build <a
 * href="http://mathworld.wolfram.com/Graph.html">graphs</a> of objects.
 *
 * @author Archit Shah (ashah@mit.edu)
 * @author Vadim Nasardinov (vadidmn@redhat.com)
 * @version $Date: 2003/02/19 $
 * @since   2003-01-22
 **/
public interface Graph {
    /**
     * Creates a copy of this graph.
     **/
    Graph copy();

    /**
     * Sets the graph's label.
     **/
    void setLabel(String label);

    /**
     * Returns the graph's label.
     **/
    String getLabel();

    /**
     * Adds a node to the graph.
     **/
    void addNode(Object node);

    /**
     * Returns <code>true</code> if the graph has this node.
     **/
    boolean hasNode(Object node);


    /**
     * Returns <code>true</code> if the graph has this edge.
     *
     * @pre hasNode(edge.getTail()) && hasNode(edge.getHead())
     **/
    boolean hasEdge(Graph.Edge edge);

    /**
     * Returns the count of nodes in this graph.
     **/
    int nodeCount();

    /**
     * Adds an edge to the graph.
     **/
    void addEdge(Graph.Edge edge);

    /**
     * A convenient shortcut for <code>addEdge(new Graph.Edge(tail, head,
     * label))</code>.
     *
     * @see #addEdge(Graph.Edge)
     **/
    void addEdge(Object tail, Object head, Object label);

    /**
     * Returns a list of nodes that this graph has. (Todo: this should probably
     * return a Set.)
     **/
    List getNodes();

    /**
     * Removes all nodes and edges.
     **/
    void removeAll();

    /**
     * Returns a list of outgoing edges leaving this node.
     **/
    List getOutgoingEdges(Object node);

    /**
     * Returns the number of outgoing edges this node has. A convenient shortcut
     * for <code>getOutgoingEdges(node).size()</code>.
     *
     * @see #getOutgoingEdges(Object)
     **/
    int outgoingEdgeCount(Object node);

    /**
     * @see #outgoingEdgeCount(node)
     **/
    int incomingEdgeCount(Object node);

    /**
     * @see #getOutgoingEdges(node)
     **/
    List getIncomingEdges(Object node);

    /**
     * An edge is an ordered pair of nodes with a label attached to it.  The
     * first node of the pair is called the <em>tail</em> and the second the
     * <em>head</em>.
     *
     * <p>Implementing classes are expected to supply a constructor of the form
     * <code>Graph.Edge(Object tail, Object head, Object label)</code>. </p>
     **/
    interface Edge {

        /**
         * Returns the tail node of the edge.
         *
         * @see #getHead()
         **/
        Object getTail();

        /**
         * Returns the head node of the edge.
         *
         * @see #getTail()
         **/
        Object getHead();

        /**
         * Returns the label associated with this edge. The label can be
         * anything, depending on your particular graph. For example, if your
         * nodes represent cities and edges represent freeways, then the label
         * can be an <code>Float</code> representing the the distance (the
         * length of the route).
         **/
        Object getLabel();

    }
}
