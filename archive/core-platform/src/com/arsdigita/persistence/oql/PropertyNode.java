package com.arsdigita.persistence.oql;

import com.arsdigita.persistence.metadata.*;
import java.util.*;

/**
 * PropertyNode
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 **/

class PropertyNode extends Node {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/oql/PropertyNode.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    private Property m_property;

    public PropertyNode(Node parent, Property property) {
        super(parent, (ObjectType) property.getType());
        m_property = property;
    }

    String getName() {
        return getParent().getName() + "." + m_property.getName();
    }

    String getAlias() {
        return getParent().getAlias() + "_" + m_property.getName();
    }

    String getPrefix() {
        return getParent().getPrefix() + m_property.getName() + ".";
    }

    Query getQuery() {
        return getParent().getQuery();
    }

    boolean isOuter() {
        if (m_property.isNullable()) {
            return true;
        } else {
            return getParent().isOuter();
        }
    }

    void buildQuery() {
        super.buildQuery();

        Query query = getQuery();
        JoinPath jp = m_property.getJoinPath();
        List path = jp.getPath();

        JoinElement first = (JoinElement) path.get(0);
        Table table = getParent().defineTable(first.getFrom().getTableName());
        Column from = table.defineColumn(first.getFrom().getColumnName());
        table = defineTable(first.getTo().getTableName());
        Column to = table.defineColumn(first.getTo().getColumnName());

        query.addCondition(new Condition(from, to));

        for (int i = 1; i < path.size(); i++) {
            JoinElement je = (JoinElement) path.get(i);
            table = defineTable(je.getFrom().getTableName());
            from = table.defineColumn(je.getFrom().getColumnName());
            table = defineTable(je.getTo().getTableName());
            to = table.defineColumn(je.getTo().getColumnName());
            query.addCondition(new Condition(from, to));
        }
    }

}
