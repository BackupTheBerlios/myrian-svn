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
package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.Event;
import com.redhat.persistence.common.Path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Operation
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

abstract class Operation {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/engine/rdbms/Operation.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private static final Logger LOG = Logger.getLogger(Operation.class);

    private RDBMSEngine m_engine;
    private Environment m_env;
    private HashSet m_parameters = new HashSet();
    private HashMap m_mappings = new HashMap();

    // For profiling
    private ArrayList m_events = null;

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

    abstract void write(SQLWriter w);

    public String toString() {
        SQLWriter w = new ANSIWriter();
        w.setEngine(m_engine);
        w.write(this);
        return w.getSQL() + "\n" + w.getBindings();
    }

    public final String toSafeString() {
        StringBuffer buf = new StringBuffer(128);
        buf.append("env=").append(m_env);
        buf.append("\nparameters=").append(m_parameters);
        buf.append("\nmappings=");
        if ( m_mappings == null ) {
            buf.append(m_mappings);
        } else {
            for (Iterator it=m_mappings.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry entry = (Map.Entry) it.next();
                buf.append("\n  ");
                buf.append(entry.getKey()).append("=");
                buf.append(Arrays.asList((Path[]) entry.getValue()));
            }
        }
        return buf.toString();
    }
}
