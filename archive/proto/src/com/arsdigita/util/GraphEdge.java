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
 * Graph edge.
 *
 * @author Archit Shah (ashah@mit.edut)
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2003-01-22
 * @version $Revision: #1 $ $Date: 2003/03/04 $
 **/
public final class GraphEdge implements Graph.Edge {
    private Object m_tail;
    private Object m_head;
    private Object m_label;

    public GraphEdge(Object tail, Object head, Object label) {
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
        if (other instanceof Graph.Edge) {
            Graph.Edge edge = (Graph.Edge) other;
            return
                this.m_tail.equals(edge.getTail()) &&
                this.m_head.equals(edge.getHead()) &&
                this.m_label.equals(edge.getLabel());
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
