package com.arsdigita.persistence.oql;

import com.arsdigita.util.*;
import java.util.*;

/**
 * Condition
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/06/10 $
 **/

class Condition {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/oql/Condition.java#2 $ by $Author: rhs $, $DateTime: 2002/06/10 15:35:38 $";

    private Set m_columns = new HashSet();
    private Query m_query;
    private Column m_tail;
    private Column m_head;

    public Condition(Query query, Column tail, Column head) {
        Assert.assertNotNull(query, "query");
        Assert.assertNotNull(tail, "tail");
        Assert.assertNotNull(head, "head");

        m_query = query;
        m_tail = tail;
        m_head = head;

        m_columns.add(tail);
        m_columns.add(head);

        m_query.addCondition(this);
        m_tail.getTable().addCondition(this);
        m_head.getTable().addCondition(this);
    }

    public void remove() {
        m_query.removeCondition(this);
        m_tail.getTable().removeCondition(this);
        m_head.getTable().removeCondition(this);
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
