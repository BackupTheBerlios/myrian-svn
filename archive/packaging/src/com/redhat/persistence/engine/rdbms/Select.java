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

package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.common.*;
import com.redhat.persistence.*;

import java.util.*;

/**
 * Select
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/08/29 $
 **/

class Select extends Operation {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/engine/rdbms/Select.java#3 $ by $Author: rhs $, $DateTime: 2003/08/29 10:31:35 $";

    private Join m_join;
    private Expression m_filter;
    private ArrayList m_selections = new ArrayList();
    private HashMap m_aliases = new HashMap();
    private ArrayList m_order = new ArrayList();
    private HashSet m_ascending = new HashSet();
    private Integer m_offset = null;
    private Integer m_limit = null;
    private boolean m_isCount = false;

    public Select(RDBMSEngine engine, Join join, Expression filter) {
        this(engine, join, filter, new Environment(engine, null));
    }

    public Select(RDBMSEngine engine, Join join, Expression filter,
                  Environment env) {
        super(engine, env);
        m_join = join;
        m_filter = filter;
    }

    public Join getJoin() {
        return m_join;
    }

    public Expression getFilter() {
        return m_filter;
    }

    public void addSelection(Path path, String alias) {
        if (!m_selections.contains(path)) {
            m_selections.add(path);
            m_aliases.put(path, alias);
        }
    }

    public String getAlias(Path path) {
        return (String) m_aliases.get(path);
    }

    public Collection getSelections() {
        return m_selections;
    }

    public void setCount(boolean value) {
        m_isCount = value;
    }

    public boolean isCount() {
        return m_isCount;
    }

    public void addOrder(Expression expr, boolean isAscending) {
        if (m_order.contains(expr)) {
            throw new IllegalArgumentException
                ("already ordered by this path");
        }

        m_order.add(expr);
        if (isAscending) {
            m_ascending.add(expr);
        }
    }

    public boolean isAscending(Expression expr) {
        return m_ascending.contains(expr);
    }

    public Collection getOrder() {
        return m_order;
    }

    public Integer getOffset() {
        return m_offset;
    }

    public void setOffset(Integer offset) {
        m_offset = offset;
    }

    public Integer getLimit() {
        return m_limit;
    }

    public void setLimit(Integer limit) {
        m_limit = limit;
    }

    void write(SQLWriter w) {
        w.write(this);
    }

}
