
/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.util.*;
import java.util.*;

/**
 * OldCondition
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/02/05 $
 **/

class OldCondition {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/OldCondition.java#1 $ by $Author: rhs $, $DateTime: 2003/02/05 18:34:37 $";

    private Set m_columns = new HashSet();
    private Node m_node;
    private Column m_tail;
    private Column m_head;
    private Query m_query;

    public OldCondition(Node node, Column tail, Column head) {
        Assert.assertNotNull(node, "node");
        Assert.assertNotNull(tail, "tail");
        Assert.assertNotNull(head, "head");

        m_node = node;
        m_tail = tail;
        m_head = head;
        m_query = node.getQuery();

        m_columns.add(tail);
        m_columns.add(head);

        m_node.addCondition(this);
        m_tail.getTable().addCondition(this);
        m_head.getTable().addCondition(this);
        m_query.addCondition(this);
    }

    public void remove() {
        m_node.removeCondition(this);
        m_tail.getTable().removeCondition(this);
        m_head.getTable().removeCondition(this);
        m_query.removeCondition(this);
    }

    public Column getTail() {
        return m_tail;
    }

    public Column getHead() {
        return m_head;
    }

    public boolean isOuter() {
        return m_head.getTable().getNode().isOuter();
    }

    public String toString() {
        return m_columns.toString();
    }

    public int hashCode() {
        return m_columns.hashCode();
    }

    public boolean equals(Object other) {
        if (other instanceof OldCondition) {
            return m_columns.equals(((OldCondition) other).m_columns);
        } else {
            return super.equals(other);
        }
    }

}
