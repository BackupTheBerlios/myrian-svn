package com.redhat.persistence.jdo;

import com.redhat.persistence.metadata.*;
import com.redhat.persistence.oql.*;
import com.redhat.persistence.oql.Query;
import java.io.Serializable;
import java.util.*;
import javax.jdo.*;

class JDOQuery implements javax.jdo.Query, Serializable {

    transient private PersistenceManagerImpl m_pm = null;
    transient private JDOQLParser m_parser;

    transient private final List m_importedPackages = new ArrayList();
    transient private final Map m_importedNames = new HashMap();
    transient private final Map m_paramMap = new HashMap();
    transient private final List m_paramOrder = new ArrayList();
    transient private final Map m_varMap = new HashMap();

    private String m_extent = null;
    private String m_params = null;
    private String m_vars = null;
    private String m_imports = null;
    private String m_filter = null;
    private String m_order = null;

    public JDOQuery(PersistenceManagerImpl pmi) {
        m_pm = pmi;
        m_parser = new JDOQLParser();
    }

    private Root getRoot() {
        return m_pm.getSession().getRoot();
    }

    private JDOQLParser getParser() {
        return m_parser;
    }

    private void reset() {
        m_importedPackages.clear();
        m_importedPackages.add("java.lang.");
        m_importedNames.clear();
        m_paramMap.clear();
        m_paramOrder.clear();
        m_varMap.clear();
    }

    public void compile() {
        JDOQLParser p = getParser();
        p.parseImports(this, m_imports);
        p.parseParameters(this, m_params);
        p.parseVariables(this, m_vars);
    }

    void addImportedName(String name, String fqn) {
        String current = (String) m_importedNames.get(name);
        if (current == null) {
            m_importedNames.put(name, fqn);
        } else if (!current.equals(fqn)) {
            throw new JDOUserException
                ("import of " + fqn + " conflicts with " + current);
        }
    }

    void addImportedPackage(String pkg) {
        m_importedPackages.add(pkg + ".");
    }

    void addParameter(String type, String name) {
        if ("this".equals(name)) {
            throw new JDOUserException("'this' is an illegal parameter name");
        }

        Class c = resolveType(type);

        if (m_paramMap.containsKey(name)) {
            throw new JDOUserException
                ("repeat declaration of parameter " + name);
        }

        m_paramMap.put(name, type);
        m_paramOrder.add(name);
    }

    void addVariable(String type, String name) {
        if ("this".equals(name)) {
            throw new JDOUserException("'this' is an illegal variable name");
        }

        Class c = resolveType(type);

        if (m_varMap.containsKey(name)) {
            throw new JDOUserException
                ("repeat declaration of variable " + name);
        }

        // XXX not using any adapter ness
        ObjectType t = getRoot().getObjectType(c.getName());
        m_varMap.put(name, t);
    }

    Class resolveType(String name) {
        String current = (String) m_importedNames.get(name);
        if (current != null) {
            try {
                return Class.forName(current);
            } catch (ClassNotFoundException cnfe) {
                throw new JDOUserException
                    ("type " + name + " resolves to " + current +
                     "which can not be found");
            }
        }

        try {
            return Class.forName(name);
        } catch (ClassNotFoundException cnfe) {
            // continue
        }

        Class match = null;
        for (int i = 0; i < m_importedPackages.size(); i++) {
            // picking first valid match
            String s = (String) m_importedPackages.get(i);
            Class candidate;
            try {
                candidate = Class.forName(s + name);
            } catch (ClassNotFoundException cnfe) {
                continue;
            }

            if (current != null) {
                throw new JDOUserException
                    (name + " could be either " + current + " or " + match);
            }

            match = candidate;
        }

        if (match == null) {
            throw new JDOUserException("could not resolve type " + name);
        }

        return match;
    }


    public void declareImports(String imports) { m_imports = imports; }

    public void declareParameters(String params) { m_params = params; }

    public void declareVariables(String vars) { m_vars = vars; }

    public Object execute() {
        if (m_paramMap.size() > 0) {
            throw new JDOUserException
                ("can not execute with unbound parameters " +
                 m_paramMap.keySet());
        }
        return execute(Collections.EMPTY_MAP);
    }

    public Object execute(Object p1) {
        if (m_paramMap.size() > 1) {
            throw new JDOUserException
                ("can not execute with unbound parameters " +
                 m_paramMap.keySet());
        }
        Map m = new HashMap();
        m.put(m_paramOrder.get(0), p1);
        return executeWithMap(m);
    }

    public Object execute(Object p1, Object p2) {
        if (m_paramMap.size() > 2) {
            throw new JDOUserException
                ("can not execute with unbound parameters " +
                 m_paramMap.keySet());
        }
        Map m = new HashMap();
        m.put(m_paramOrder.get(0), p1);
        m.put(m_paramOrder.get(1), p1);
        return executeWithMap(m);
    }

    public Object execute(Object p1, Object p2, Object p3) {
        if (m_paramMap.size() > 3) {
            throw new JDOUserException
                ("can not execute with unbound parameters " +
                 m_paramMap.keySet());
        }
        Map m = new HashMap();
        m.put(m_paramOrder.get(0), p1);
        m.put(m_paramOrder.get(1), p1);
        m.put(m_paramOrder.get(2), p1);
        return executeWithMap(m);
    }

    public Object executeWithMap(Map params) {
        JDOQLParser p = getParser();
        Expression expr = new All(m_extent);

        for (Iterator it = m_varMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            String name = (String) me.getKey();
            ObjectType type = (ObjectType) me.getValue();
            // join here?
        }

        Expression filter = p.filter(this, m_filter, params);
        throw new Error("not implemented");
    }

    public Object executeWithArray(Object[] params) {
        if (params.length != m_paramMap.size()) {
            throw new JDOUserException
                ("can not execute with unbound parameters " +
                 m_paramMap.keySet());
        }
        Map m = new HashMap();
        for (int i = 0; i < params.length; i++) {
            m.put(m_paramOrder.get(i), params[i]);
        }
        return executeWithMap(m);
    }

    public PersistenceManager getPersistenceManager() {
        return m_pm;
    }

    public void setCandidates(Collection pcs) {
        throw new Error("not implemented");
    }

    public void setCandidates(Extent pcs) {
        throw new Error("not implemented");
    }

    public void setClass(Class cls) {
        m_extent = cls.getName();
    }

    public void setFilter(String filter) {
        m_filter = filter;
    }

    public void setOrdering(String ordering) {
        m_order = ordering;
    }

    public boolean getIgnoreCache() { return false; }

    public void setIgnoreCache(boolean ignoreCache) {
        throw new Error("not implemented");
    }

    public void close(Object queryResult) {
        throw new Error("not implemented");
    }

    public void closeAll() {
        throw new Error("not implemented");
    }
}
