package com.redhat.persistence.engine.rdbms;

import com.arsdigita.developersupport.DeveloperSupport;
import com.arsdigita.webdevsupport.WebDevSupport;
import com.redhat.persistence.*;
import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;

import java.util.*;
import java.sql.*;

import org.apache.log4j.Logger;

/**
 * RDBMSEngine
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/08/07 $
 **/

public class RDBMSEngine extends Engine {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/engine/rdbms/RDBMSEngine.java#3 $ by $Author: bche $, $DateTime: 2003/08/07 17:11:17 $";

    private static final Logger LOG = Logger.getLogger(RDBMSEngine.class);


    private ArrayList m_operations = new ArrayList();
    private HashMap m_operationMap = new HashMap();
    private EventSwitch m_switch = new EventSwitch(this);
    private Event m_event = null;
    private HashMap m_environments = new HashMap();
    private ArrayList m_mutations = new ArrayList();
    private ArrayList m_mutationTypes = new ArrayList();

    private ConnectionSource m_source;
    private Connection m_conn = null;
    private int m_connUsers = 0;

    private SQLWriter m_writer;
    private RDBMSProfiler m_profiler;

    public RDBMSEngine(ConnectionSource source, SQLWriter writer) {
        this(source, writer, null);
    }

    public RDBMSEngine(ConnectionSource source, SQLWriter writer,
                       RDBMSProfiler profiler) {
        m_source = source;
        m_writer = writer;
        m_profiler = profiler;
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
        DML result = (DML) m_operationMap.get(key);
        if (m_profiler != null && result != null) {
            result.addEvent(m_event);
        }
        return result;
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
        if (m_profiler != null) {
            op.addEvent(m_event);
        }
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
        if (LOG.isInfoEnabled()) {
            LOG.info("Executing " + query);
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
        if (LOG.isInfoEnabled()) {
            LOG.info("Executing size " + query);
        }
        QGen qg = new QGen(query, block);
        Select sel = qg.generate();
        sel.setCount(true);
        ResultCycle rc = execute(sel);
        if (rc == null) {
            throw new IllegalStateException
                ("null result set");
        }
        ResultSet rs = rc.getResultSet();
        StatementLifecycle cycle = rc.getLifecycle();
        try {
            try {
                long result;
                if (rc.next()) {
                    if (cycle != null) { cycle.beginGet("1"); }
                    result = rs.getLong(1);
                    if (cycle != null) { cycle.endGet(new Long(result)); }
                } else {
                    throw new IllegalStateException
                        ("count returned no rows");
                }
                if (rc.next()) {
                    throw new IllegalStateException
                        ("count returned too many rows");
                }
                if (LOG.isInfoEnabled()) {
                    LOG.info("size = " + result);
                }
                return result;
            } catch (SQLException e) {
                throw new RDBMSException(e.getMessage()) {};
            }
        } finally {
            rc.close();
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
                        m_event = ev;
                        ev.dispatch(m_switch);
                        m_event = null;
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
                ResultCycle rc = execute(op);
                if (rc != null) { rc.close(); }
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

    private ResultCycle execute(Operation op) {
        return execute(op, m_writer);
    }

    private ResultCycle execute(Operation op, SQLWriter w) {
        try {
            try {
                w.write(op);
            } catch(RDBMSException re) {
                w.clear();
                LOG.warn("failed operation: " + op);
                throw re;
            }

            String sql = w.getSQL();

            if (LOG.isInfoEnabled()) {
                LOG.info(sql);
                LOG.info(w.getBindings());
                LOG.info(w.getTypeNames());
                LOG.info(op.getEnvironment());
            }


            if (sql.equals("")) {
                return null;
            }

            acquire();
            
            /*
             * PERFORMANCE HACK: 
             * If there is more than one DeveloperSupportListener,
             * there is a listener other than then one created in com.arsdigita.dispatcher.Initializer.
             * That means we are logging queries to webdevsupport.
             */            
            boolean bLogQuery = DeveloperSupport.getListenerCount() > 1;
            if (LOG.isDebugEnabled()) {                
                LOG.debug("Logging queries: " + bLogQuery);
            }                 
            
            //see what kind of operation this isfor webdevsupport
            String sOpType = "";
            if (bLogQuery) {                
		if (op instanceof Select) {
			sOpType = "executeQuery";
		} else if (op instanceof Update) {
			sOpType = "executeUpdate";
		} else {
			sOpType = "execute";
		}
            }                
            
            try {
                StatementLifecycle cycle = null;
                if (m_profiler != null) {
                    RDBMSStatement stmt = new RDBMSStatement(sql);
                    stmt.setQuery(op.getQuery());
                    for (Iterator it = op.getEvents().iterator();
                         it.hasNext(); ) {
                        stmt.addEvent((Event) it.next());
                    }
                    cycle = m_profiler.getLifecycle(stmt);
                }

                if (cycle != null) { cycle.beginPrepare(); }
                PreparedStatement ps = m_conn.prepareStatement(sql);
                if (cycle != null) { cycle.endPrepare(); }

                w.bind(ps, cycle);

                if (cycle != null) { cycle.beginExecute(); }
                long time = System.currentTimeMillis();                
                if (ps.execute()) {
                    time = System.currentTimeMillis() - time;
                    if (cycle != null) { cycle.endExecute(0); }                       
                    if (bLogQuery) {                        
                        DeveloperSupport.logQuery(m_conn.hashCode(), sOpType, sql, collToMap(w.getBindings()), time, null);                                     
                    }                    
                    return new ResultCycle(this, ps.getResultSet(), cycle);
                } else {                    
                    time = System.currentTimeMillis() - time;
                    if (bLogQuery) {
                        DeveloperSupport.logQuery(m_conn.hashCode(), sOpType, sql, collToMap(w.getBindings()), time, null);
                    }                                                            
                    int updateCount = ps.getUpdateCount();
                    if (cycle != null) { cycle.endExecute(updateCount); }

                    if (LOG.isInfoEnabled()) {
                        LOG.info(updateCount + " rows affected");
                    }

                    if (cycle != null) { cycle.beginClose(); }
                    ps.close();
                    if (cycle != null) { cycle.endClose(); }

                    return null;
                }
            } catch (SQLException e) {
                LOG.error(sql, e);
                if (bLogQuery) {
                    DeveloperSupport.logQuery(m_conn.hashCode(), sOpType, sql, collToMap(w.getBindings()), 0, e);
                }
                throw new RDBMSException(e.getMessage()) {};
            }
        } finally {
            w.clear();
        }
    }

    public void execute(SQLBlock sql, Map parameters) {
        Environment env = new Environment(null);
        for (Iterator it = parameters.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            env.set((Path) me.getKey(), me.getValue());
        }
        Operation op = new StaticOperation(sql, env, false);
        execute(op, new RetainUpdatesWriter());
    }
    
    private HashMap collToMap(Collection c) {
        Iterator iter = c.iterator();
        HashMap map = new HashMap();
        for (int i=1; iter.hasNext(); i++) {
            map.put(new Integer(i), iter.next());
        }
        
        return map;
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
