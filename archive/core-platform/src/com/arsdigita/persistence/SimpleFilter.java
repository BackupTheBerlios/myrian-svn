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

import java.util.HashMap;
import java.util.Map;
import com.redhat.persistence.oql.Expression;
import com.redhat.persistence.oql.Static;

/**
 * SimpleFilter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/03/23 $
 **/

class SimpleFilter extends FilterImpl {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/SimpleFilter.java#4 $ by $Author: dennis $, $DateTime: 2004/03/23 03:39:40 $";

    String m_conditions;

    SimpleFilter(String conditions) {
        // note that it is possible for conditions to be null
        // if we actually want a NO-OP filter
        m_conditions = conditions;
    }

    protected Expression makeExpression(DataQueryImpl query, Map bindings) {
        String conditions = getConditions();
        if (conditions == null) {
            return null;
        }

        conditions = query.unalias(conditions);
        conditions = query.mapAndAddPaths(conditions);
        try {
            Map map;
            if (bindings.size() > 0) {
                map = new HashMap();
                map.putAll(bindings);
                map.putAll(getBindings());
            } else {
                map = getBindings();
            }

            return new Static(conditions, map);
        } catch (RuntimeException re) {
            System.err.println("original conditions: " + getConditions());
            throw re;
        }
    }

    /**
     *  This returns the SQL that is represented by the Filter. All
     *  values in the filter should have been bound with
     *  set(parameterName, value).  This actually returns the
     *  conditions with a namespace constant inserted after the ":"
     *  so that we know what namespace to use for binding.
     **/

    public String getConditions() {
	return m_conditions;
    }

    /**
     * This prints out a string representation of the filter
     */
    public String toString() {
        return "Filter:" + Utilities.LINE_BREAK +
            " Conditions: " + m_conditions +
            Utilities.LINE_BREAK + "  Values: " + getBindings();
    }

}
