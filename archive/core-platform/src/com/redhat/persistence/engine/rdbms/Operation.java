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

import com.redhat.persistence.Event;
import com.redhat.persistence.Query;
import com.redhat.persistence.common.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Operation
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2003/10/28 $
 **/

abstract class Operation {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/engine/rdbms/Operation.java#6 $ by $Author: jorris $, $DateTime: 2003/10/28 18:36:21 $";

    private static final Logger LOG = Logger.getLogger(Operation.class);

    private RDBMSEngine m_engine;
    private Environment m_env;
    private HashSet m_parameters = new HashSet();
    private HashMap m_mappings = new HashMap();

    // For profiling
    private ArrayList m_events = null;
    private Query m_query = null;

    protected Operation(RDBMSEngine engine, Environment env) {
        m_engine = engine;
        m_env = env;
    }

    protected Operation(RDBMSEngine engine) {
        this(engine, new Environment(engine, null));
    }

    public boolean isParameter(Path path) {
        return m_parameters.contains(path);
    }

    public void addParameter(Path path) {
        m_parameters.add(path);
    }

    public boolean contains(Path parameter) {
        return m_env.contains(parameter);
    }

    public void set(Path parameter, Object value) {
        m_parameters.add(parameter);
        m_env.set(parameter, value);
    }

    public void set(Path parameter, Object value, int type) {
        m_parameters.add(parameter);
        m_env.set(parameter, value, type);
    }

    public Object get(Path parameter) {
        return m_env.get(parameter);
    }

    public int getType(Path parameter) {
        return m_env.getType(parameter);
    }

    Environment getEnvironment() {
        return m_env;
    }

    public Path[] getMapping(Path p) {
        return (Path[]) m_mappings.get(p);
    }

    public void setMapping(Path p, Path[] cols) {
        m_mappings.put(p, cols);
    }

    public void setMappings(Map map) {
        m_mappings.putAll(map);
    }

    void addEvent(Event ev) {
        if (ev == null) { throw new IllegalArgumentException("null event"); }
        if (m_events == null) { m_events = new ArrayList(); }
        if (!m_events.contains(ev)) {
            m_events.add(ev);
        }
    }

    Collection getEvents() {
        if (m_events == null) {
            return Collections.EMPTY_LIST;
        } else {
            return m_events;
        }
    }

    void setQuery(Query query) {
        m_query = query;
    }

    Query getQuery() {
        return m_query;
    }

    abstract void write(SQLWriter w);

    public String toString() {
        SQLWriter w = new ANSIWriter();
        w.setEngine(m_engine);
        w.write(this);
        return w.getSQL() + "\n" + w.getBindings();
    }

}
