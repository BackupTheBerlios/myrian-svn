package com.arsdigita.persistence.oql;

import com.arsdigita.util.*;
import java.util.*;

/**
 * Condition
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 **/

class Condition {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/oql/Condition.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    private Set m_columns = new HashSet();
    private Column m_left;
    private Column m_right;

    public Condition(Column left, Column right) {
        Assert.assertNotNull(left, "left");
        Assert.assertNotNull(right, "right");

        m_left = left;
        m_right = right;

        m_columns.add(left);
        m_columns.add(right);

        m_left.getTable().addCondition(this);
        m_right.getTable().addCondition(this);
    }

    public Column getLeft() {
        return m_left;
    }

    public Column getRight() {
        return m_right;
    }

    public boolean isOuter() {
        return m_right.getTable().getNode().isOuter();
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
