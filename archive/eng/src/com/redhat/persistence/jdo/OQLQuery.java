package com.redhat.persistence.jdo;

import com.redhat.persistence.Session;
import com.redhat.persistence.Signature;
import com.redhat.persistence.metadata.*;
import com.redhat.persistence.oql.*;
import java.util.*;
import javax.jdo.*;

public class OQLQuery implements javax.jdo.Query {

    private boolean m_ignoreCache = false;
    transient private final PersistenceManagerImpl m_pmi;
    private final String m_query;
    private String m_filter = null;
    transient private final List m_addPaths = new ArrayList();

    OQLQuery(PersistenceManagerImpl pmi, String query) {
        m_pmi = pmi;
        m_query = query;
    }

    public PersistenceManager getPersistenceManager() {
        return m_pmi;
    }

    public void addPath(String path) {
        m_addPaths.add(path);
    }

    private Expression makeExpr(Map params) {
        Map parameters = new HashMap(params);
        for (Iterator it = parameters.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            if (me.getValue() instanceof OQLCollection) {
                OQLCollection c = (OQLCollection) me.getValue();
                me.setValue(c.expression());
            }
        }

        Expression expr = Expression.valueOf(m_query, parameters);

        if (m_filter != null) {
            expr = new Define(expr, "this");
            expr = new Get
                (new Filter(expr, Expression.valueOf(m_filter)), "this");
        }

        return expr;
    }

    public Object executeWithMap(Map parameters) {
        final Expression expr = makeExpr(parameters);
        final ObjectType type = expr.getType(m_pmi.getSession().getRoot());
        final Signature sig = new Signature(type);
        for (Iterator it = m_addPaths.iterator(); it.hasNext(); ) {
            sig.addPath((String) it.next());
        }

        return new CRPCollection() {
            protected Signature signature() { return sig; }
            Session ssn() { return m_pmi.getSession(); }
            ObjectType type() { return type; }
            public Expression expression() { return expr; }
            public boolean add(Object o) {
                throw new UnsupportedOperationException();
            }
            public boolean remove(Object o) {
                throw new UnsupportedOperationException();
            }
            public void clear() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public Object executeWithArray(Object[] parameters) {
        Map m = new HashMap();
        for (int i = 0; i < parameters.length; i++) {
            m.put("$" + (i+1), parameters[i]);
        }
        return executeWithMap(m);
    }

    public Object execute() {
        return executeWithArray(new Object[0]);
    }

    public Object execute(Object p1) {
        return executeWithArray(new Object[] {p1});
    }

    public Object execute(Object p1, Object p2) {
        return executeWithArray(new Object[] {p1, p2});
    }

    public Object execute(Object p1, Object p2, Object p3) {
        return executeWithArray(new Object[] {p1, p2, p3});
    }

    public void compile() {
        // do nothing
    }

    public void closeAll() {
        throw new Error("not implemented");
    }

    public void close(Object result) {
        throw new Error("not implemented");
    }

    public boolean getIgnoreCache() {
        return m_ignoreCache;
    }

    public void setIgnoreCache(boolean value) {
        m_ignoreCache = value;
    }

    public void declareVariables(String variables) {
        throw new Error("not implemented");
    }

    public void declareParameters(String parameters) {
        throw new Error("not implemented");
    }

    public void declareImports(String imports) {
        throw new Error("not implemented");
    }

    public void setCandidates(Collection pcs) {
        throw new Error("not implemented");
    }

    public void setCandidates(Extent pcs) {
        throw new Error("not implemented");
    }

    public void setClass(Class pcs) {
        throw new Error("not implemented");
    }

    public void setFilter(String filter) {
        m_filter = filter;
    }

    public void setOrdering(String ordering) {
        throw new Error("not implemented");
    }
}
