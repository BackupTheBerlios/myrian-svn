package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.*;
import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;

import org.apache.log4j.Logger;

import java.util.*;

/**
 * Operation
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 **/

abstract class Operation {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/engine/rdbms/Operation.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

    private static final Logger LOG = Logger.getLogger(Operation.class);

    private Environment m_env;
    private HashSet m_parameters = new HashSet();
    private HashMap m_mappings = new HashMap();

    // For profiling
    private ArrayList m_events = null;
    private Query m_query = null;

    protected Operation(Environment env) {
        m_env = env;
    }

    protected Operation() {
        this(new Environment(null));
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
        w.write(this);
        return w.getSQL() + "\n" + w.getBindings();
    }

}
