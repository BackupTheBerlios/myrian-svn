package com.arsdigita.persistence.oql;

import com.arsdigita.persistence.metadata.*;
import java.util.*;

/**
 * PropertyNode
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/05/21 $
 **/

class PropertyNode extends Node {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/oql/PropertyNode.java#2 $ by $Author: rhs $, $DateTime: 2002/05/21 20:57:49 $";

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
        Query query = getQuery();
        JoinPath jp = m_property.getJoinPath();
        List path = jp.getPath();

        JoinElement first = (JoinElement) path.get(0);
        Table table = getParent().defineTable(first.getFrom().getTableName());
        Column from = table.defineColumn(first.getFrom());
        table = defineTable(first.getTo().getTableName());
        Column to = table.defineColumn(first.getTo());

        query.addCondition(new Condition(from, to));

        for (int i = 1; i < path.size(); i++) {
            JoinElement je = (JoinElement) path.get(i);
            table = defineTable(je.getFrom().getTableName());
            from = table.defineColumn(je.getFrom());
            table = defineTable(je.getTo().getTableName());
            to = table.defineColumn(je.getTo());
            query.addCondition(new Condition(from, to));
        }

        super.buildQuery();
    }

}
