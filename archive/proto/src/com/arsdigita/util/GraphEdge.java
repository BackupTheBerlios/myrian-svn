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
 * @version $Revision: #2 $ $Date: 2003/05/08 $
 **/
public final class GraphEdge implements Graph.Edge {
    private Object m_tail;
    private Object m_head;
    private Object m_label;

    /**
     * @pre tail != null
     * @pre head != null
     **/
    public GraphEdge(Object tail, Object head, Object label) {
        Assert.exists(tail, Object.class);
        Assert.exists(head, Object.class);
        m_tail = tail;
        m_head = head;
        m_label = label;
    }

    /**
     * @set return != null
     **/
    public Object getTail() {
        return m_tail;
    }

    /**
     * @set return != null
     **/
    public Object getHead() {
        return m_head;
    }

    public Object getLabel() {
        return m_label;
    }

    public String toString() {
        return m_label.toString();
    }

    public boolean equals(Object obj) {
        if ( obj == null ) return false;

        if (obj instanceof Graph.Edge) {
            Graph.Edge that = (Graph.Edge) obj;
            Object thatLabel = that.getLabel();

            boolean equalLabels =
                (m_label == null && thatLabel == null ) ||
                (m_label != null && m_label.equals(thatLabel)) ||
                (thatLabel != null && thatLabel.equals(m_label));

            return
                m_tail.equals(that.getTail()) &&
                m_head.equals(that.getHead()) &&
                equalLabels;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return m_tail.hashCode() + m_head.hashCode() +
            (m_label == null ? 0 : m_label.hashCode());
    }
}
