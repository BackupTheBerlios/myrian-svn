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

package com.arsdigita.persistence.oql;

import com.arsdigita.util.*;
import com.arsdigita.persistence.metadata.*;

/**
 * Selection
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 **/

class Selection {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/oql/Selection.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

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

    protected void setProperty(Property property) {
        m_property = property;
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
        Mapping mapping = new Mapping(StringUtils.split(path, '.'),
                                      m_column.getTable().getAlias(),
                                      getAlias());
        mapping.setLineInfo(m_property);
        return mapping;
    }


    public String getName() {
        return m_node.getName() + "." + getProperty().getName();
    }

    public String getAlias() {
        if (m_node == getQuery() &&
            m_node.getObjectType().isKeyProperty(getProperty())) {
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
