package com.arsdigita.persistence.oql;

import com.arsdigita.util.*;
import com.arsdigita.persistence.metadata.*;

/**
 * Selection
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2002/06/10 $
 **/

class Selection {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/oql/Selection.java#4 $ by $Author: rhs $, $DateTime: 2002/06/10 15:35:38 $";

    private Node m_node;
    private Property m_property;
    private Column m_column;

    Selection(Node node, Property property) {
        m_node = node;
        m_property = property;

        getQuery().addSelection(this);
    }

    public Query getQuery() {
        return m_node.getQuery();
    }

    public Property getProperty() {
        return m_property;
    }

    public Column getColumn() {
        return m_column;
    }

    public void setColumn(Column column) {
        if (m_column != null) {
            m_column.getTable().removeSelection(this);
        }
        m_column = column;
        m_column.getTable().addSelection(this);
    }

    public Mapping getMapping() {
        String path = m_node.getPrefix() + m_property.getName();
        com.arsdigita.persistence.metadata.Column col =
            new com.arsdigita.persistence.metadata.Column(
                m_column.getTable().getAlias(),
                getAlias()
                );
        col.setLineInfo(m_property.getColumn());
        Mapping mapping = new Mapping(StringUtils.split(path, '.'), col);
        mapping.setLineInfo(m_property);
        return mapping;
    }


    public String getName() {
        return m_node.getName() + "." + m_property.getName();
    }

    public String getAlias() {
        if (m_node == getQuery() &&
            m_node.getObjectType().isKeyProperty(m_property)) {
            return m_column.getName();
        } else {
            String alias = m_node.getAlias();
            if (alias == null) {
                alias = m_column.getTable().getName();
            } else {
                alias = alias + "__" + m_column.getTable().getName();
            }

            alias = alias + "__" + m_column.getName();

            return m_node.getQuery().abbreviate(alias);
        }
    }

    public String toString() {
        return getName() + " -> " + m_column;
    }

}
