package com.arsdigita.persistence.proto.metadata;

import com.arsdigita.persistence.proto.common.*;

import java.util.*;

/**
 * ObjectMap
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #13 $ $Date: 2003/03/05 $
 **/

public class ObjectMap extends Element {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/ObjectMap.java#13 $ by $Author: rhs $, $DateTime: 2003/03/05 18:41:57 $";

    private ObjectType m_type;
    private Mist m_mappings = new Mist(this);
    private ArrayList m_key = new ArrayList();
    private ArrayList m_fetched = new ArrayList();
    private Table m_table;

    private SQLBlock m_retrieveAll;
    private ArrayList m_retrieves = new ArrayList();
    private ArrayList m_inserts = new ArrayList();
    private ArrayList m_updates = new ArrayList();
    private ArrayList m_deletes = new ArrayList();

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

    public Collection getKeyProperties() {
        ObjectMap sm = getSuperMap();
        if (sm == null) {
            return m_key;
        } else {
            return sm.getKeyProperties();
        }
    }

    public Collection getFetchedPaths() {
        final HashSet result = new HashSet();
        for (Iterator it = getMappings().iterator(); it.hasNext(); ) {
            Mapping m = (Mapping) it.next();
            m.dispatch(new Mapping.Switch() {
                    public void onValue(Value m) {
                        result.add(m.getPath());
                    }

                    public void onJoinTo(JoinTo m) {
                        result.add(m.getPath());
                    }

                    public void onJoinFrom(JoinFrom m) {}

                    public void onJoinThrough(JoinThrough m) {}

                    public void onStatic(Static m) {}
                });
        }

        result.addAll(m_fetched);

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
        return getTables(getObjectType().getDeclaredProperties());
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

    public Collection getRetrieves() {
        if (getSuperMap() == null) {
            return m_retrieves;
        } else {
            ArrayList result = new ArrayList();
            result.addAll(getSuperMap().getRetrieves());
            result.addAll(m_retrieves);
            return result;
        }
    }

    public Collection getDeclaredInserts() {
        return m_inserts;
    }

    public Collection getInserts() {
        if (getSuperMap() == null) {
            return m_inserts;
        } else {
            ArrayList result = new ArrayList();
            result.addAll(getSuperMap().getInserts());
            result.addAll(m_inserts);
            return result;
        }
    }

    public Collection getDeclaredUpdates() {
        return m_updates;
    }

    public Collection getUpdates() {
        if (getSuperMap() == null) {
            return m_updates;
        } else {
            ArrayList result = new ArrayList();
            result.addAll(getSuperMap().getUpdates());
            result.addAll(m_updates);
            return result;
        }
    }

    public Collection getDeclaredDeletes() {
        return m_deletes;
    }

    public Collection getDeletes() {
        if (getSuperMap() == null) {
            return m_deletes;
        } else {
            ArrayList result = new ArrayList();
            result.addAll(m_deletes);
            result.addAll(getSuperMap().getDeletes());
            return result;
        }
    }

    public void addDelete(SQLBlock delete) {
        m_deletes.add(delete);
    }

    Object getElementKey() {
        return getObjectType();
    }

}
