package com.redhat.persistence.oql;

import java.util.*;

/**
 * Node
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/01/16 $
 **/

abstract class Node {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Node.java#1 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

    private List m_outputs = new ArrayList();

    Node() {}

    void add(Node input) {
        if (!input.m_outputs.contains(this)) {
            input.m_outputs.add(this);
        }
    }

    abstract boolean update();

    private static class NodeQueue {
        private LinkedList m_queue = new LinkedList();
        private HashSet m_enqueued = new HashSet();

        public boolean isEmpty() {
            return m_enqueued.isEmpty();
        }

        public void enqueue(Node node) {
            if (!m_enqueued.contains(node)) {
                m_queue.add(node);
                m_enqueued.add(node);
            }
        }

        public Node dequeue() {
            Node node = (Node) m_queue.removeFirst();
            m_enqueued.remove(node);
            return node;
        }

        public void enqueue(Collection nodes) {
            for (Iterator it = nodes.iterator(); it.hasNext(); ) {
                Node node = (Node) it.next();
                enqueue(node);
            }
        }

    }

    static void propogate(Collection nodes) {
        NodeQueue queue = new NodeQueue();

        for (Iterator it = nodes.iterator(); it.hasNext(); ) {
            Node node = (Node) it.next();
            queue.enqueue(node.m_outputs);
        }

        while (!queue.isEmpty()) {
            Node node = queue.dequeue();
            if (node.update()) {
                queue.enqueue(node.m_outputs);
            }
        }
    }

}
