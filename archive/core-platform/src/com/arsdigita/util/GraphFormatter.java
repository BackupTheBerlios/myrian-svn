/*
 * Copyright (C) 2003, 2003 Red Hat Inc. All Rights Reserved.
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

/**
 * Implementations of this interface can be used for pretty-printing graphs.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @version $Date: 2003/08/15 $
 * @since 2003-01-23
 **/
public interface GraphFormatter {

    /**
     * Returns graph attributes.
     * 
     * <p>For example, if you choose to pretty-print your graph in the DOT
     * language, then the graph attributes section may look like so:</p>
     *
     * <pre>
     *  digraph mygraph {
     *     // the following two lines are graph attributes
     *     node[shape=box,fontsize=8,fontname=verdana,height=0.2,width=0.2,style=filled];
     *     ranksep=0.05;
     *
     *     // the following lines are nodes and edges
     *     A -> B -> C -> D;
     *     B -> E -> F;
     *     C -> G;
     *     D -> I;
     *     D -> J -> H;
     *  }

     **/
    String graphAttributes(Graph graph);

    /**
     * Returns a textual representation of the node, preferably a short one that
     * can be used in the following plain-text representation of the tree.
     *
     * <pre>
     * digraph tree {
     *     A -> B -> C -> D;
     *     B -> E -> F;
     *     C -> G;
     *     D -> I;
     *     D -> J -> H;
     * }
     * </pre>
     * 
     * <p>Example implementation:</p>
     *
     * <pre>
     *  public String formatNode(Object node) {
     *      return node == null ? null : ((ObjectType) node).getName();
     *  }
     * </pre>
     **/
    String nodeName(Object node);

    /**
     * Returns [bracketed] node attributes.
     *
     * <pre>
     *  digraph g {
     *      C<strong>[label="The C Language"]</strong>;
     *      J<strong[label="The Java Language"]</strong>;
     *      C -> J;
     *  }
     * </pre>
     **/
    String nodeAttributes(Object node);

    /**
     * Returns a short textual label describing the edge.
     **/
    String edge(Object edge);
}
