package com.redhat.persistence.oql;

import java.util.*;

/**
 * Propogator
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/01/20 $
 **/

class Propogator {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Propogator.java#1 $ by $Author: rhs $, $DateTime: 2004/01/20 12:41:29 $";

    private NodeQueue m_queue = new NodeQueue();

    void clear() {
        m_queue.clear();
    }

    void add(Node node) {
        m_queue.enqueue(node);
    }

    List nodes() {
        return m_queue.nodes();
    }

    boolean step() {
        if (!m_queue.isEmpty()) {
            Node node = m_queue.dequeue();
            if (node.update()) {
                m_queue.enqueue(node.getOutputs());
            }
        }
        return !m_queue.isEmpty();
    }

    void propogate() {
        while (step()) {}
    }

    private static class NodeQueue {
        private LinkedList m_queue = new LinkedList();
        private HashSet m_enqueued = new HashSet();

        public boolean isEmpty() {
            return m_enqueued.isEmpty();
        }

        public List nodes() {
            return m_queue;
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

        public void clear() {
            m_queue.clear();
            m_enqueued.clear();
        }

    }

}
