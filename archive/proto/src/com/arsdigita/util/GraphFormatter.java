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

/**
 * Implementations of this interface can be used for pretty-printing graphs.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @version $Date: 2003/02/19 $
 * @since 2003-01-23
 **/
public interface GraphFormatter {
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
    String formatNode(Object node);

    /**
     * Returns a short textual label describing the edge.
     **/
    String formatEdge(Object edge);
}
