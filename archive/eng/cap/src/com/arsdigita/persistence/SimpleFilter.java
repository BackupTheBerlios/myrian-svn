/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
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
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

class SimpleFilter extends FilterImpl {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/SimpleFilter.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
