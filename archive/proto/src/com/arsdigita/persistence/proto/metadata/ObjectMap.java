package com.arsdigita.persistence.proto.metadata;

import com.arsdigita.persistence.proto.common.*;

import java.util.*;

/**
 * ObjectMap
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #10 $ $Date: 2003/02/19 $
 **/

public class ObjectMap extends Element {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/ObjectMap.java#10 $ by $Author: rhs $, $DateTime: 2003/02/19 22:58:51 $";

    private ObjectType m_type;
    private Mist m_mappings = new Mist(this);
    private ArrayList m_key = new ArrayList();
    private Join m_superJoin;
    private ArrayList m_joins = new ArrayList();
    private ArrayList m_fetched = new ArrayList();
    private SQLBlock m_retrieve;
    private ArrayList m_inserts = new ArrayList();
    private ArrayList m_updates = new ArrayList();
    private ArrayList m_deletes = new ArrayList();

    public ObjectMap(ObjectType type) {
        m_type = type;
    }

    public Root getRoot() {
        return (Root) getParent();
    }

    ObjectMap getSuperMap() {
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
        HashSet result = new HashSet();
        for (Iterator it = getMappings().iterator(); it.hasNext(); ) {
            Mapping m = (Mapping) it.next(); 
            if (m.isValue()) {
                result.add(m.getPath());
            }
        }

        result.addAll(m_fetched);

        return result;
    }

    public void addFetchedPath(Path p) {
        if (!m_fetched.contains(p)) {
            m_fetched.add(p);
        }
    }

    public void setSuperJoin(Join join) {
        m_superJoin = join;
    }

    public Join getSuperJoin() {
        return m_superJoin;
    }

    public Collection getAllJoins() {
        ArrayList result = new ArrayList();

        if (getSuperMap() != null) {
            result.addAll(getSuperMap().getAllJoins());
        }

        if (getSuperJoin() != null) {
            result.add(getSuperJoin());
        }
        result.addAll(getJoins());

        return result;
    }

    public Collection getJoins() {
        return m_joins;
    }

    public void addJoin(Join join) {
        m_joins.add(join);
    }

    private Collection getDeclaredJoins() {
        ArrayList joins = new ArrayList();
        if (m_superJoin != null) {
            joins.add(m_superJoin);
        }
        joins.addAll(m_joins);
        return joins;
    }

    public Table getTable() {
        Join sup = getSuperJoin();
        if (sup == null) {
            Property key = (Property) getKeyProperties().iterator().next();
            Mapping m = getMapping(Path.get(key.getName()));
            if (m == null) { return null; }
            if (m.isValue()) {
                return ((ValueMapping) m).getColumn().getTable();
            } else {
                return ((ReferenceMapping) m).getJoin(0).getFrom().getTable();
            }
        } else {
            return sup.getFrom().getTable();
        }
    }

    public Collection getTables() {
        final ArrayList result = new ArrayList();
        for (Iterator it = getObjectType().getProperties().iterator();
             it.hasNext(); ) {
            Property prop = (Property) it.next();
            Mapping m = getMapping(Path.get(prop.getName()));
            // XXX: no metadata
            if (m == null) { continue; }
            m.dispatch(new Mapping.Switch() {
                    public void onValue(ValueMapping vm) {
                        Table t = vm.getColumn().getTable();
                        if (!result.contains(t)) {
                            result.add(t);
                        }
                    }

                    public void onReference(ReferenceMapping rm) {
                        if (rm.isJoinTo()) {
                            Table t = rm.getJoin(0).getFrom().getTable();
                            if (!result.contains(t)) {
                                result.add(t);
                            }
                        }
                    }
                });
        }
        return result;
    }

    public SQLBlock getRetrieve() {
        return m_retrieve;
    }

    public void setRetrieve(SQLBlock retrieve) {
        m_retrieve = retrieve;
    }

    public Collection getInserts() {
        return m_inserts;
    }

    public void addInsert(SQLBlock insert) {
        m_inserts.add(insert);
    }

    public Collection getUpdates() {
        return m_updates;
    }

    public void addUpdate(SQLBlock update) {
        m_updates.add(update);
    }

    public Collection getDeletes() {
        return m_deletes;
    }

    public void addDelete(SQLBlock delete) {
        m_deletes.add(delete);
    }

    Object getKey() {
        return getObjectType();
    }

}
