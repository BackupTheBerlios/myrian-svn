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
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class RDBMSEngine extends Engine {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/engine/rdbms/RDBMSEngine.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    private static final Logger LOG = Logger.getLogger(RDBMSEngine.class);


    private EventStream m_events = new EventStream(true);
    private HashMap m_outgoing = new HashMap();
    private HashMap m_counts = new HashMap();

    private ArrayList m_operations = new ArrayList();
    private HashMap m_operationMap = new HashMap();
    private EventSwitch m_generator = new EventSwitch(this);
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
        ArrayList ops = (ArrayList) m_operationMap.get(obj);
        if (ops != null) {
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
        clearEvents();
        clearOperations();
        m_mutations.clear();
        m_mutationTypes.clear();
    }

    void clearEvents() {
        m_events.clear();
        m_outgoing.clear();
        m_counts.clear();
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

    private Collection getOutgoing(Event ev) {
        return (Collection) m_outgoing.get(ev);
    }

    private int getCount(Event ev) {
        if (m_counts.containsKey(ev)) {
            return ((Integer) m_counts.get(ev)).intValue();
        } else {
            return 0;
        }
    }

    private void setCount(Event ev, int count) {
        if (count == 0) {
            m_counts.remove(ev);
        } else {
            m_counts.put(ev, new Integer(count));
        }
    }

    private void addArrow(Event from, Event to) {
        LinkedList outgoing = (LinkedList) m_outgoing.get(from);
        if (outgoing == null) {
            outgoing = new LinkedList();
            m_outgoing.put(from, outgoing);
        }

        setCount(to, getCount(to) + 1);

        outgoing.add(to);
    }

    private void clearArrows(Event ev) {
        Collection outgoing = getOutgoing(ev);
        if (outgoing == null) { return; }
        for (Iterator it = outgoing.iterator(); it.hasNext(); ) {
            Event to = (Event) it.next();
            setCount(to, getCount(to) - 1);
        }
        m_outgoing.remove(ev);
    }

    private void addObjectDependency(Event ev, Object obj) {
        ObjectEvent oe = m_events.getLastEvent(obj);
        if (oe != null) {
            addArrow(oe, ev);
        }
    }

    private void addLinkDependency(final Property prop, final ObjectEvent from,
                                   final ObjectEvent to) {
        if (from == null || to == null) {
            return;
        }

        ObjectType ot = prop.getContainer();
        ObjectMap om = ot.getRoot().getObjectMap(ot);
        Mapping m = om.getMapping(Path.get(prop.getName()));
        m.dispatch(new Mapping.Switch() {
                public void onValue(Value m) { }

                public void onJoinTo(JoinTo m) {
                    addArrow(to, from);
                }

                public void onJoinFrom(JoinFrom m) {
                    addArrow(from, to);
                }

                public void onJoinThrough(JoinThrough m) {
                    // do nothing
                }

                public void onStatic(Static m) {
                    if (prop.isComponent()) {
                        addArrow(from, to);
                    } else if (!prop.isCollection()) {
                        addArrow(to, from);
                    }
                }
            });
    }

    public void write(Event ev) {
        addObjectDependency(ev, ev.getObject());

        ev.dispatch(new Event.Switch() {
                public void onCreate(CreateEvent e) { }
                public void onDelete(DeleteEvent e) {
                    Collection pes =
                        m_events.getReachablePropertyEvents(e.getObject());
                    for (Iterator it = pes.iterator(); it.hasNext(); ) {
                        PropertyEvent pe = (PropertyEvent) it.next();
                        addArrow(pe, e);
                        Object arg;
                        if (pe instanceof SetEvent) {
                            if (pe.getArgument() != null) {
                                throw new IllegalStateException
                                    ("nonnull set arg reachable from delete");
                            }
                            arg = ((SetEvent) pe).getPreviousValue();
                        } else {
                            arg = pe.getArgument();
                        }

                        addLinkDependency
                            (pe.getProperty(), m_events.getLastEvent(arg), e);
                    }
                }

                private void onProperty(PropertyEvent e) {
                    Object obj = e.getObject();
                    Property prop = e.getProperty();
                    Object arg = e.getArgument();

                    addObjectDependency(e, arg);

                    PropertyEvent pe = m_events.getLastEvent(obj, prop);
                    if (pe != null) { addArrow(pe, e); }

                    ObjectEvent objev = m_events.getLastEvent(obj);
                    ObjectEvent argev = m_events.getLastEvent(arg);

                    addLinkDependency(prop, objev, argev);
                }

                public void onSet(SetEvent e) { onProperty(e); }
                public void onAdd(AddEvent e) { onProperty(e); }
                public void onRemove(RemoveEvent e) { onProperty(e); }
            });

        m_events.add(ev);
    }

    private void generate() {
        HashSet generated = new HashSet();
        int before;
        do {
            before = generated.size();

            for (Iterator it = m_events.iterator(); it.hasNext(); ) {
                Event ev = (Event) it.next();
                if (generated.contains(ev)) { continue; }
                if (getCount(ev) == 0) {
                    generated.add(ev);
                    clearArrows(ev);
                    ev.dispatch(m_generator);
                }
            }
        } while (generated.size() > before);

        if (generated.size() < m_events.size()) {
            throw new IllegalStateException
                ("not all events flushed");
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
        LinkedList result = new LinkedList();
        LinkedList stack = new LinkedList();
        stack.add(prefix);

        while (stack.size() > 0) {
            Path p = (Path) stack.removeLast();

            ObjectType ot = type.getType(Path.relative(prefix, p));
            Collection keys = ot.getKeyProperties();
            if (keys.size() == 0) {
                result.add(p);
                continue;
            }

            ArrayList revKeys = new ArrayList(keys.size());
            revKeys.addAll(keys);
            Collections.reverse(revKeys);

            for (Iterator it = revKeys.iterator(); it.hasNext(); ) {
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