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

import com.arsdigita.persistence.metadata.*;
import java.util.*;

/**
 * PropertyNode
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 **/

class PropertyNode extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/oql/PropertyNode.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    private Property m_property;

    public PropertyNode(Node parent, Property property) {
        super(parent, (ObjectType) property.getType());
        m_property = property;
        fetchKey();
    }

    String getName() {
        return getParent().getName() + "." + m_property.getName();
    }

    String getAlias() {
        String alias = getParent().getAlias();
        if (alias == null) {
            alias = m_property.getName();
        } else {
            alias = alias + "_" + m_property.getName();
        }
        return  alias;
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

    boolean isNullable() {
        return m_property.isNullable();
    }

    void error(String message) {
        m_property.error(message);
    }

    void buildQuery() {
        // FIXME: is the variable "query" instantiated for side effects? --
        // 2002-11-26
        Query query = getQuery();
        JoinPath jp = m_property.getJoinPath();
        List path = jp.getPath();

        JoinElement first = (JoinElement) path.get(0);
        Table table = getParent().defineTable(first.getFrom().getTableName());
        Column from = table.defineColumn(first.getFrom());
        table = defineTable(first.getTo().getTableName());
        Column to = table.defineColumn(first.getTo());

        new Condition(this, from, to);

        for (int i = 1; i < path.size(); i++) {
            JoinElement je = (JoinElement) path.get(i);
            table = defineTable(je.getFrom().getTableName());
            from = table.defineColumn(je.getFrom());
            table = defineTable(je.getTo().getTableName());
            to = table.defineColumn(je.getTo());
            new Condition(this, from, to);
        }

        super.buildQuery();
    }
}
