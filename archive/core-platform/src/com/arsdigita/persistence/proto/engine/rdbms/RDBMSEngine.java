package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;
import java.sql.*;

import org.apache.log4j.Logger;

/**
 * RDBMSEngine
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2003/06/26 $
 **/

public class RDBMSEngine extends Engine {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/engine/rdbms/RDBMSEngine.java#5 $ by $Author: rhs $, $DateTime: 2003/06/26 18:40:22 $";

    private static final Logger LOG = Logger.getLogger(RDBMSEngine.class);


    private ArrayList m_operations = new ArrayList();
    private HashMap m_operationMap = new HashMap();
    private EventSwitch m_switch = new EventSwitch(this);
    private HashMap m_environments = new HashMap();
    private ArrayList m_mutations = new ArrayList();
    private ArrayList m_mutationTypes = new ArrayList();

    private ConnectionSource m_source;
    private Connection m_conn = null;
    private int m_connUsers = 0;

    private SQLWriter m_writer;

    public RDBMSEngine(ConnectionSource source, SQLWriter writer) {
        m_source = source;
        m_writer = writer;
    }

    void acquire() {
        if (m_conn == null) {
            m_conn = m_source.acquire();
        }
	m_connUsers++;
    }

    void release() {
	if (m_conn == null) {
	    return;
	}

	m_connUsers--;

	if (m_connUsers == 0) {
            m_source.release(m_conn);
            m_conn = null;
        }
    }

    void releaseAll() {
	if (m_conn != null) {
	    m_source.release(m_conn);
	    m_conn = null;
	    m_connUsers = 0;
	}
    }

    void addOperation(Object obj, DML dml) {
        Object key = new CompoundKey(obj, dml.getTable());
        if (dml instanceof Delete) {
            DML prev = (DML) m_operationMap.get(key);
            if (prev != null) {
		removeOperation(obj, prev);
            }
        }
        m_operationMap.put(key, dml);
        addOperation(dml);
    }

    void removeOperation(Object obj, DML dml) {
        Object key = new CompoundKey(obj, dml.getTable());
	m_operationMap.remove(key);
	m_operations.remove(dml);
    }

    DML getOperation(Object obj, Table table) {
        Object key = new CompoundKey(obj, table);
        return (DML) m_operationMap.get(key);
    }

    void clearUpdates(Object obj) {
	m_operationMap.remove(obj);
    }

    void removeUpdates(Object obj) {
        LOG.debug("Removing updates for: " + obj);
        ArrayList ops = (ArrayList) m_operationMap.get(obj);
        if (ops != null) {
            LOG.debug("found: " + ops);
            for (Iterator it = ops.iterator(); it.hasNext(); ) {
                Operation op = (Operation) it.next();
		if (op instanceof DML) {
		    removeOperation(obj, (DML) op);
		} else {
		    m_operations.remove(op);
		}
                it.remove();
            }
        }
	clearUpdates(obj);
    }

    void markUpdate(Object obj) {
	if (!hasUpdates(obj)) {
	    m_operationMap.put(obj, new ArrayList());
	}
    }

    void markUpdate(Object obj, Operation op) {
	markUpdate(obj);
	ArrayList ops = (ArrayList) m_operationMap.get(obj);
	ops.add(op);
    }

    boolean hasUpdates(Object obj) {
	return m_operationMap.containsKey(obj);
    }

    void addOperation(Operation op) {
        m_operations.add(op);
    }

    Environment getEnvironment(Object obj) {
        Environment result = (Environment) m_environments.get(obj);
        if (result == null) {
            result = new Environment(Session.getObjectMap(obj));
            m_environments.put(obj, result);
        }
        return result;
    }

    void scheduleMutation(SetEvent e, int type) {
        m_mutations.add(e);
        m_mutationTypes.add(new Integer(type));
    }

    void clear() {
        m_aggregator.clear();
        clearOperations();
        m_mutations.clear();
        m_mutationTypes.clear();
    }

    void clearOperations() {
        m_operationMap.clear();
        m_operations.clear();
        m_environments.clear();
    }

    protected void commit() {
        acquire();
        try {
            m_conn.commit();
        } catch (SQLException e) {
            throw new Error(e.getMessage());
        } finally {
            releaseAll();
        }
    }

    protected void rollback() {
        acquire();
        try {
            m_conn.rollback();
        } catch (SQLException e) {
            throw new Error(e.getMessage());
        } finally {
            releaseAll();
            clear();
        }
    }

    public RecordSet execute(Query query) {
	return execute(query, null);
    }

    public RecordSet execute(Query query, SQLBlock block) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Executing " + query);
        }
        QGen qg = new QGen(query, block);
        Select sel = qg.generate();
        return new RDBMSRecordSet(query.getSignature(), this, execute(sel),
                                  qg.getMappings(sel));
    }

    public long size(Query query) {
        return size(query, null);
    }

    public long size(Query query, SQLBlock block) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Executing size " + query);
        }
        QGen qg = new QGen(query, block);
        Select sel = qg.generate();
        sel.setCount(true);
        ResultSet rs = execute(sel);
        if (rs == null) {
            throw new IllegalStateException
                ("null result set");
        }
        try {
            try {
                long result;
                if (rs.next()) {
                    result = rs.getLong(1);
                } else {
                    throw new IllegalStateException
                        ("count returned no rows");
                }
                if (rs.next()) {
                    throw new IllegalStateException
                        ("count returned too many rows");
                }
                return result;
            } catch (SQLException e) {
                throw new RDBMSException(e.getMessage()) {};
            }
        } finally {
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug
                        ("Closing Statement because resultset was closed.");
                }
                rs.getStatement().close();
                rs.close();
            } catch (SQLException e) {
                throw new RDBMSException(e.getMessage()) {};
            }
        }
    }

    private Aggregator m_aggregator = new Aggregator();

    public void write(Event ev) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(ev);
        }
        ev.dispatch(m_aggregator);
    }

    private void generate() {
        Collection nodes = m_aggregator.getNodes();
        HashSet generated = new HashSet();
        int before;
        do {
            before = generated.size();

            for (Iterator it = nodes.iterator(); it.hasNext(); ) {
                Node nd = (Node) it.next();
                if (generated.containsAll(nd.getDependencies())) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Generating node: " + nd);
                    }
                    int ops = m_operations.size();
                    Collection events = nd.getEvents();
                    for (Iterator iter = events.iterator(); iter.hasNext(); ) {
                        Event ev = (Event) iter.next();
                        if (generated.contains(ev)) {
                            throw new IllegalStateException
                                ("event generated twice: " + ev);
                        }
                        ev.dispatch(m_switch);
                        generated.add(ev);
                    }
                    for (int i = ops; i < m_operations.size(); i++) {
                        Operation op = (Operation) m_operations.get(i);
                        LOG.debug("GENERATED: " + op);
                        LOG.debug("ENV: " + op.getEnvironment());
                    }
                    it.remove();
                    m_operationMap.clear();
                    m_environments.clear();
                }
            }
        } while (generated.size() > before);

        if (nodes.size() > 0) {
            StringBuffer msg = new StringBuffer();
            msg.append("unable to generate all events:");
            for (Iterator it = nodes.iterator(); it.hasNext(); ) {
                Node nd = (Node) it.next();
                msg.append("\n\nnode {");
                msg.append("\n  events {");
                for (Iterator iter = nd.getEvents().iterator();
                     iter.hasNext(); ) {
                    msg.append("\n    ");
                    msg.append(iter.next());
                }
                msg.append("\n  }\n");
                msg.append("\n  unresolved dependencies {");
                for (Iterator iter = nd.getDependencies().iterator();
                     iter.hasNext(); ) {
                    Event ev = (Event) iter.next();
                    if (!generated.contains(ev)) {
                        msg.append("\n    ");
                        msg.append(ev);
                    }
                }
                msg.append("\n  }");
                msg.append("\n}");
            }
            throw new IllegalStateException(msg.toString());
        }
    }

    private RDBMSQuerySource QS = new RDBMSQuerySource();

    public void flush() {
        try {
            generate();

            for (Iterator it = m_operations.iterator(); it.hasNext(); ) {
                Operation op = (Operation) it.next();
                it.remove();
                ResultSet rs = execute(op);
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        throw new Error(e.getMessage());
                    }
                }
            }

            for (int i = 0; i < m_mutations.size(); i++) {
                SetEvent e = (SetEvent) m_mutations.get(i);
                int jdbcType = ((Integer) m_mutationTypes.get(i)).intValue();
                Property prop = e.getProperty();
                RDBMSRecordSet rs = (RDBMSRecordSet) execute
                    (QS.getQuery(e.getObject(), prop));
                Adapter ad = Adapter.getAdapter(prop.getType());
                try {
                    if (rs.next()) {
                        ad.mutate(rs.getResultSet(),
                                  rs.getColumn(Path.get(prop.getName())),
                                  e.getArgument(),
                                  jdbcType);
                    } else {
                        throw new IllegalStateException
                            ("cannot update blob");
                    }
                } catch (SQLException se) {
                    throw new Error(se.getMessage());
                } finally {
                    rs.close();
                }
            }
        } finally {
            clear();
        }
    }

    private ResultSet execute(Operation op) {
        return execute(op, m_writer);
    }

    private ResultSet execute(Operation op, SQLWriter w) {
        try {
            try {
                w.write(op);
            } catch(RDBMSException re) {
                w.clear();
                LOG.warn("failed operation: " + op);
                throw re;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug(w.getSQL());
                LOG.debug(w.getBindings());
                LOG.debug(w.getTypeNames());
                LOG.debug(op.getEnvironment());
            }

            if (w.getSQL().equals("")) {
                return null;
            }

            acquire();

            try {
                PreparedStatement ps = m_conn.prepareStatement(w.getSQL());
                w.bind(ps);
                if (ps.execute()) {
                    return ps.getResultSet();
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(ps.getUpdateCount() + " rows affected");
                    }
                    ps.close();
                    return null;
                }
            } catch (SQLException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(m_operations);
                }
                throw new RDBMSException(e.getMessage()) {};
            }
        } finally {
            w.clear();
        }
    }

    public ResultSet execute(SQLBlock sql, Map parameters) {
        Environment env = new Environment(null);
        for (Iterator it = parameters.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            env.set((Path) me.getKey(), me.getValue());
        }
        Operation op = new StaticOperation(sql, env, false);
        return execute(op, new RetainUpdatesWriter());
    }

    static final Path[] getKeyPaths(ObjectType type, Path prefix) {
        return getPaths(type, prefix, false);
    }

    static final Path[] getImmediatePaths(ObjectType type, Path prefix) {
        return getPaths(type, prefix, true);
    }

    private static final Path[] getPaths(ObjectType type, Path prefix,
                                 boolean immediate) {
        LinkedList result = new LinkedList();
        LinkedList stack = new LinkedList();
        stack.add(prefix);

        while (stack.size() > 0) {
            Path p = (Path) stack.removeLast();

            ObjectType ot = type.getType(Path.relative(prefix, p));
            Collection props;
            if (immediate) {
                props = ot.getImmediateProperties();
            } else {
                props = ot.getKeyProperties();
            }
            if (props.size() == 0) {
                result.add(p);
                continue;
            }

            ArrayList revProps = new ArrayList(props.size());
            revProps.addAll(props);
            Collections.reverse(revProps);

            for (Iterator it = revProps.iterator(); it.hasNext(); ) {
                Property key = (Property) it.next();
                stack.add(Path.add(p, key.getName()));
            }
        }

        return (Path[]) result.toArray(new Path[0]);
    }

    static final Object get(Object obj, Path path) {
        if (path == null) {
            return obj;
        }

        Object o = get(obj, path.getParent());
        if (o == null) {
            return null;
        }

        PropertyMap props = Session.getProperties(o);
        return props.get(props.getObjectType().getProperty(path.getName()));
    }

    static final int getType(Object obj) {
        if (obj == null) {
            return Types.INTEGER;
        } else {
            return getType(obj.getClass());
        }
    }

    static final int getType(Class klass) {
        return Adapter.getAdapter(klass).defaultJDBCType();
    }

}
