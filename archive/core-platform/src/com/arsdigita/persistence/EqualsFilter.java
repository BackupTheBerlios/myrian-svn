/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.persistence;

import java.util.Map;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.oql.Equals;
import com.redhat.persistence.oql.Expression;
import com.redhat.persistence.oql.Literal;
import com.redhat.persistence.oql.Not;
import com.redhat.persistence.oql.Static;

class EqualsFilter extends FilterImpl {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/EqualsFilter.java#2 $ by $Author: ashah $, $DateTime: 2004/03/24 01:05:38 $";

    private final String m_attribute;
    private final String m_bindName;
    private final boolean m_not;

    static FilterImpl eq(String attribute, Object value) {
        return new EqualsFilter(attribute, value, false);
    }

    static FilterImpl notEq(String attribute, Object value) {
        return new EqualsFilter(attribute, value, true);
    }

    private EqualsFilter(String attribute, Object value, boolean not) {
        m_attribute = attribute;
        m_not = not;
        m_bindName = value == null ? null : FilterImpl.bindName(m_attribute);

        if (m_bindName != null) {
            set(m_bindName, value);
        }
    }

    private boolean isValueNull() {
        return m_bindName == null;
    }

    protected Expression makeExpression(DataQueryImpl query, Map bindings) {
        Path path = Path.get(m_attribute);
        path = query.unalias(path);

        Expression variable;
        if (query.hasProperty(path)) {
            path = query.mapAndAddPath(path);
            variable = Expression.valueOf(path);
        } else {
            // this handles cases like eq("lower(attribute)", value)
            String expr = query.unalias(m_attribute);
            expr = query.mapAndAddPaths(expr);
            variable = new Static(expr);
        }

        Expression value;
        if (isValueNull()) {
            value = new Literal(null);
        } else {
            value = new Literal(getBindings().get(m_bindName));
        }


        Expression expr = new Equals(variable, value);

        if (m_not) {
            expr = new Not(expr);
        }

        return expr;
    }

    public String getConditions() {
        String connector = m_not ? "!=" : "=";
        String conditions;
        if (isValueNull()) {
            conditions = FilterImpl.createNullString(connector, m_attribute);
        } else {
            conditions = m_attribute + connector + " :" + m_bindName;
        }

	return conditions;
    }

    public String toString() {
        return "Equals Filter: " + m_attribute + " = " + m_bindName +
            Utilities.LINE_BREAK + "  Values: " + getBindings();
    }

}
