package com.arsdigita.util;

import java.util.*;

import com.arsdigita.util.Assert;

public class Graph {
    private final static String LINE_SEP = System.getProperty("line.separator");

    private Set m_nodes = new HashSet();
    private Map m_outgoingEdges = new HashMap();
    private Map m_incomingEdges = new HashMap();
    private String m_label = "directed_graph";

    public Graph copy() {
        Graph newGraph = new Graph();
        for (Iterator nodes = getNodes().iterator(); nodes.hasNext(); ) {
            Object node = nodes.next();
            newGraph.addNode(node);
            for (Iterator edges=getOutgoingEdges(node).iterator(); edges.hasNext(); ) {
                Edge edge = (Edge) edges.next();
                newGraph.addEdge(edge.getTail(), edge.getHead(), edge.getLabel());
            }
        }
        newGraph.setLabel(getLabel());
        return newGraph;
    }

    public void setLabel(String label) {
        Assert.assertTrue(label !=null, "label is not null");
        m_label = label;
    }

    public String getLabel() {
        return m_label;
    }

    public void addNode(Object name) {
        m_nodes.add(name);
    }

    public boolean hasNode(Object nodeName) {
        return m_nodes.contains(nodeName);
    }

    /**
     * @pre hasNode(edge.getTail()) && hasNode(edge.getHead())
     **/
    public boolean hasEdge(Graph.Edge edge) {
        return outgoingEdges(edge.getTail()).contains(edge);
    }

    public int nodeCount() {
        return m_nodes.size();
    }

    public void addEdge(Graph.Edge edge) {
        m_nodes.add(edge.getTail());
        m_nodes.add(edge.getHead());
        outgoingEdges(edge.getTail()).add(edge);
        incomingEdges(edge.getHead()).add(edge);
    }

    public void addEdge(Object tail, Object head, Object label) {
        addEdge(new Edge(tail, head, label));
    }

    public List getNodes() {
        return new ArrayList(m_nodes);
    }

    private Set outgoingEdges(Object nodeName) {
        Set edges = (Set) m_outgoingEdges.get(nodeName);
        if (edges == null) {
            edges = new HashSet(4);
            m_outgoingEdges.put(nodeName, edges);
        }

        return edges;
    }

    private static String objToString(Object obj) {
        return obj == null ? "null" : obj.toString();
    }

    public List getOutgoingEdges(Object node) {
        Assert.assertTrue(hasNode(node), objToString(node));
        return new ArrayList(outgoingEdges(node));
    }

    public int outgoingEdgeCount(Object node) {
        Assert.assertTrue(hasNode(node), objToString(node));
        return outgoingEdges(node).size();
    }

    public int incomingEdgeCount(Object node) {
        Assert.assertTrue(hasNode(node), objToString(node));
        return incomingEdges(node).size();
    }

    private Set incomingEdges(Object nodeName) {
        Set edges = (Set) m_incomingEdges.get(nodeName);
        if ( edges == null ) {
            edges = new HashSet();
            m_incomingEdges.put(nodeName, edges);
        }
        return edges;
    }

    public Iterator getIncomingEdges(Object node) {
        Assert.assertTrue(hasNode(node), objToString(node));
        return incomingEdges(node).iterator();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("graph ").append(getLabel()).append(" {");
        sb.append(LINE_SEP);
        List sortedNodes = new ArrayList(m_nodes);
        Collections.sort(sortedNodes);
        for (Iterator nodes=sortedNodes.iterator(); nodes.hasNext(); ) {
            Object node = nodes.next();
            for (Iterator edges = getOutgoingEdges(node).iterator(); edges.hasNext(); ) {
                Graph.Edge edge = (Graph.Edge) edges.next();
                sb.append("    ");
                sb.append(objToString(node)).append(" -> ");
                sb.append(objToString(edge.getHead()));
                sb.append("[label=\"").append(edge.getLabel());
                sb.append("\"];");
                sb.append(LINE_SEP);
            }
        }
        sb.append("}");
        return sb.toString();
    }

    public static class Edge {
        private Object m_tail;
        private Object m_head;
        private Object m_label;

        Edge(Object tail, Object head, Object label) {
            m_tail = tail;
            m_head = head;
            m_label = label;
        }

        public Object getTail() {
            return m_tail;
        }

        public Object getHead() {
            return m_head;
        }

        public Object getLabel() {
            return m_label;
        }

        public String toString() {
            return m_label.toString();
        }

        public boolean equals(Object other) {
            if (other instanceof Edge) {
                Edge o = (Edge) other;
                return
                    this.m_tail.equals(o.m_tail) &&
                    this.m_head.equals(o.m_head) &&
                    this.m_label.equals(o.m_label);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return hashCode(m_tail) + hashCode(m_head) + hashCode(m_label);
        }

        private static int hashCode(Object obj) {
            return obj == null ? 0 : obj.hashCode();
        }
    }
}
