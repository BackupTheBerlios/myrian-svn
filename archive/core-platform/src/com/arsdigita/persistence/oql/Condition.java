package com.arsdigita.persistence.oql;

import com.arsdigita.util.*;
import java.util.*;

/**
 * Condition
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2002/07/18 $
 **/

class Condition {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/oql/Condition.java#4 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

    private Set m_columns = new HashSet();
    private Node m_node;
    private Column m_tail;
    private Column m_head;
    private Query m_query;

    public Condition(Node node, Column tail, Column head) {
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
        if (other instanceof Condition) {
            return m_columns.equals(((Condition) other).m_columns);
        } else {
            return super.equals(other);
        }
    }

}
