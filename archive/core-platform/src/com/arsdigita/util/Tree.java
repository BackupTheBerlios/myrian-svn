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
import java.util.Iterator;
import java.util.List;

/**
 * This class a represents the <a
 * href="http://mathworld.wolfram.com/Tree.html">tree</a> abstraction. This
 * implementation takes a recursive definition where a tree is a root node
 * connected to other (sub)trees.
 *
 * <p>This implementation allows the same node to be used in more than position
 * in the tree. For example, you can do something like this: </p>
 *
 * <pre>
 *  Tree aa = new Tree("a");
 *  Tree bb = aa.addChild("b");
 *  bb.addChild("a");
 *  aa.addChild("c");
 * </pre>
 *
 * <p>This can be visualized as follows:</p>
 *
 * <pre>
 *    a
 *   / \
 *  /   \
 * b     c
 *  \
 *   \
 *    a
 * </pre>
 *
 * <p>The only ways to add node to the tree is through the {@link #Tree(Object)
 * constructor} and the {@link #addChild(Object)} {@link #addChild(Object,
 * Object)} methods. </p>
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @version $Date: 2003/01/23 $
 * @since 2003-01-23
 **/
public class Tree {
    private final Tree m_parent;
    private final Object m_root;
    private final List m_children;
    private String m_label;

    /**
     * @param root the root node of the tree
     * @pre root != null
     **/
    public Tree(Object root) {
        this(null, root);
    }

    private Tree(Tree parent, Object root) {
        m_parent = parent;
        m_root = root;
        m_children = new ArrayList();
    }

    public void setLabel(String label) {
        m_label = label;
    }

    public String getLabel() {
        return m_label;
    }

    /**
     * Returns the root of this tree.
     **/
    public Object getRoot() {
        return m_root;
    }

    /**
     * Adds a child element to the root of this tree. Returns the subtree rooted
     * at the newly created node.
     **/
    public Tree addChild(Object child, Object edge) {
        Tree tree = new Tree(this, child);
        m_children.add(new EdgeTreePair(edge, tree));
        return tree;
    }

    /**
     * A shortcut for <code>addChild(child, null)</code>.
     *
     * @see #addChild(Object, Object)
     **/
    public Tree addChild(Object child) {
        return addChild(child, null);
    }

    /**
     * Returns the tree rooted at the parent node of the root of this tree or
     * <code>null</code>, if the root of this tree has no parent node.
     * 
     **/
    public Tree getParent() {
        return m_parent;
    }

    /**
     * Returns the list of {@link Tree.EdgeTreePair edge-tree pairs} parented to
     * the root node of this tree in the order in which they were initially
     * added. Manipulating the returned list does not affect this tree. For
     * example, if you remove an element from the list, no changes are made to
     * this tree.
     **/
    public List getSubtrees() {
        return new ArrayList(m_children);
    }

    /**
     * Returns the list of trees such that each of the returned trees is rooted
     * at the ancestor nodes of this tree or an empty list, if the root of this
     * tree has no ancestors. The closer ancestors appear first in the list.
     **/
    public List getAncestors() {
        List result = new ArrayList();
        if ( getParent() == null ) {
            return result;
        }
        ancestorsRecurse(getParent(), result);
        return result;
    }

    private static void ancestorsRecurse(Tree node, List accumulator) {
        accumulator.add(node);
        if ( node.getParent() != null ) {
            ancestorsRecurse(node.getParent(), accumulator);
        }
    }

    /**
     * Takes a list of trees and returns a new list where each tree from the
     * passed in list is replaced with its root node.
     *
     * @pre trees != null
     **/
    public static List treesToNodes(List trees) {
        Assert.assertNotNull(trees, "trees");
        List result = new ArrayList();
        for (Iterator ii=trees.iterator(); ii.hasNext(); ) {
            result.add(((Tree) ii.next()).getRoot());
        }
        return result;
    }

    /**
     * Nodes in a tree are connected with edges. An edge can be object that
     * characterizes the relationship between the parent and the child nodes.
     * For example, if you use the tree to represent the composition structure
     * of XSLT stylesheets, then the edge object may be a string with one of two
     * possible values: "xsl:import" and "xsl:include", allowing you to
     * distinguish the method by which the parent stylesheet incorporates the
     * child.
     *
     * <p>The edge-tree pair class represents an order pair where the first
     * element is the edge, and the second is the subtree rooted at the head
     * node of the edge.</p>
     **/
    public static class EdgeTreePair {
        private Object m_edge;
        private Tree m_tree;

        private EdgeTreePair(Object edge, Tree tree) {
            m_edge = edge;
            m_tree = tree;
        }

        public Object getEdge() {
            return m_edge;
        }

        /**
         * Returns the subtree rooted at the head node of the edge.
         **/
        public Tree getTree() {
            return m_tree;
        }
    }

    /**
     * Implementations of this interface can be used for pretty-printing the
     * tree.
     **/
    public interface Formatter {
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
}