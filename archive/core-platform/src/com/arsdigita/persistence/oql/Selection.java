package com.arsdigita.persistence.oql;

import com.arsdigita.util.*;
import com.arsdigita.persistence.metadata.*;

/**
 * Selection
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 **/

class Selection {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/oql/Selection.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    private Node m_node;
    private Property m_property;
    private Column m_column;

    Selection(Node node, Property property) {
        m_node = node;
        m_property = property;
    }

    public Property getProperty() {
        return m_property;
    }

    public Column getColumn() {
        return m_column;
    }

    public void setColumn(Column column) {
        m_column = column;
    }

    public String getAlias() {
        if (m_node == m_node.getQuery() &&
            m_node.getObjectType().isKeyProperty(m_property)) {
            return m_property.getColumn().getColumnName();
        } else {
            return m_node.getQuery().abbreviate(
                m_node.getAlias() + "__" + m_column.getTable().getName() +
                "__" + m_column.getName()
                );
        }
    }

    public Mapping getMapping() {
        String path = m_node.getPrefix() + m_property.getName();
        return new Mapping(StringUtils.split(path, '.'),
                           new com.arsdigita.persistence.metadata.Column(
                               m_column.getTable().getAlias(),
                               getAlias()
                               ));
    }

}
