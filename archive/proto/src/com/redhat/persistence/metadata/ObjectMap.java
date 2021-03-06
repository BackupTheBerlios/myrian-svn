package com.redhat.persistence.metadata;

import com.redhat.persistence.common.*;

import java.util.*;

/**
 * ObjectMap
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public class ObjectMap extends Element {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/metadata/ObjectMap.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    private ObjectType m_type;
    private Mist m_mappings = new Mist(this);
    private ArrayList m_key = new ArrayList();
    private ArrayList m_fetched = new ArrayList();
    private Table m_table;

    private SQLBlock m_retrieveAll;
    private ArrayList m_retrieves = null;
    private ArrayList m_inserts = null;
    private ArrayList m_updates = null;
    private ArrayList m_deletes = null;

    public ObjectMap(ObjectType type) {
        m_type = type;
    }

    public Root getRoot() {
        return (Root) getParent();
    }

    public ObjectMap getSuperMap() {
        if (m_type.getSupertype() == null) {
            return null;
        } else {
            return getRoot().getObjectMap(m_type.getSupertype());
        }
    }

    public ObjectType getObjectType() {
        return m_type;
    }

    public boolean hasMapping(Path p) {
        if (m_mappings.containsKey(p)) {
            return true;
        } else {
            ObjectMap sm = getSuperMap();
            if (sm == null) {
                return false;
            } else {
                return sm.hasMapping(p);
            }
        }
    }

    public Mapping getMapping(Path p) {
        if (m_mappings.containsKey(p)) {
            return (Mapping) m_mappings.get(p);
        } else {
            ObjectMap sm = getSuperMap();
            if (sm == null) {
                return null;
            } else {
                return sm.getMapping(p);
            }
        }
    }

    public void addMapping(Mapping mapping) {
        m_mappings.add(mapping);
    }

    private void getMappings(ArrayList result) {
        ObjectMap sm = getSuperMap();
        if (sm != null) {
            sm.getMappings(result);
        }
        result.addAll(m_mappings);
    }

    public Collection getMappings() {
        ArrayList result = new ArrayList();
        getMappings(result);
        return result;
    }

    public Collection getDeclaredMappings() {
        ArrayList result = new ArrayList();
        result.addAll(m_mappings);
        return result;
    }

    public List getKeyProperties() {
        ObjectMap sm = getSuperMap();
        if (sm == null) {
            return m_key;
        } else {
            return sm.getKeyProperties();
        }
    }

    public Collection getFetchedPaths() {
        ObjectMap sm = getSuperMap();
        if (sm == null) {
            return getDeclaredFetchedPaths();
        } else {
            Collection result = sm.getFetchedPaths();
            result.addAll(getDeclaredFetchedPaths());
            return result;
        }
    }

    public Collection getDeclaredFetchedPaths() {
        final ArrayList result = new ArrayList();
        for (Iterator it = getDeclaredMappings().iterator(); it.hasNext(); ) {
            Mapping m = (Mapping) it.next();
            m.dispatch(new Mapping.Switch() {
                    public void onValue(Value m) {
                        if (!result.contains(m.getPath())) {
                            result.add(m.getPath());
                        }
                    }

                    public void onJoinTo(JoinTo m) {}

                    public void onJoinFrom(JoinFrom m) {}

                    public void onJoinThrough(JoinThrough m) {}

                    public void onStatic(Static m) {}
                });
        }

        for (Iterator it = m_fetched.iterator(); it.hasNext(); ) {
            Object o = it.next();
            if (!result.contains(o)) {
                result.add(o);
            }
        }

        return result;
    }

    public void addFetchedPath(Path p) {
        if (!m_fetched.contains(p)) {
            m_fetched.add(p);
        }
    }

    public Table getTable() {
        return m_table;
    }

    public void setTable(Table table) {
        m_table = table;
    }

    public Collection getDeclaredTables() {
        if (m_table == null) {
            return Collections.EMPTY_LIST;
        }

        List result = new ArrayList(1);
        result.add(m_table);

        return result;
    }

    public Collection getTables() {
        return getTables(getObjectType().getProperties());
    }

    private Collection getTables(Collection properties) {
        final ArrayList result = new ArrayList();
        for (Iterator it = properties.iterator();
             it.hasNext(); ) {
            Property prop = (Property) it.next();
            Mapping m = getMapping(Path.get(prop.getName()));
	    Table t = m.getTable();
            if (t == null) { continue; }
            if (!result.contains(t)) {
                result.add(t);
            }
        }
        return result;
    }

    public SQLBlock getRetrieveAll() {
        return m_retrieveAll;
    }

    public void setRetrieveAll(SQLBlock retrieveAll) {
        m_retrieveAll = retrieveAll;
    }

    public Collection getDeclaredRetrieves() {
        return m_retrieves;
    }

    public void setDeclaredRetrieves(Collection retrieves) {
        if (retrieves == null) {
            m_retrieves = null;
        } else {
            m_retrieves = new ArrayList();
            m_retrieves.addAll(retrieves);
        }
    }

    public Collection getRetrieves() {
        if (getSuperMap() == null) {
            ArrayList result = new ArrayList();
            if (m_retrieves != null) {
                result.addAll(m_retrieves);
            }
            return result;
        } else {
            Collection result = getSuperMap().getRetrieves();
            if (m_retrieves != null) {
                result.addAll(m_retrieves);
            }
            return result;
        }
    }

    public Collection getDeclaredInserts() {
        return m_inserts;
    }

    public void setDeclaredInserts(Collection inserts) {
        if (inserts == null) {
            m_inserts = null;
        } else {
            m_inserts = new ArrayList();
            m_inserts.addAll(inserts);
        }
    }

    public Collection getDeclaredUpdates() {
        return m_updates;
    }

    public void setDeclaredUpdates(Collection updates) {
        if (updates == null) {
            m_updates = null;
        } else {
            m_updates = new ArrayList();
            m_updates.addAll(updates);
        }
    }

    public Collection getUpdates() {
        if (getSuperMap() == null) {
            ArrayList result = new ArrayList();
            if (m_updates != null) {
                result.addAll(m_updates);
            }
            return result;
        } else {
            Collection result = getSuperMap().getUpdates();
            if (m_updates != null) {
                result.addAll(m_updates);
            }
            return result;
        }
    }

    public Collection getDeclaredDeletes() {
        return m_deletes;
    }

    public void setDeclaredDeletes(Collection deletes) {
        if (deletes == null) {
            m_deletes = null;
        } else {
            m_deletes = new ArrayList();
            m_deletes.addAll(deletes);
        }
    }

    Object getElementKey() {
        return getObjectType();
    }

}
