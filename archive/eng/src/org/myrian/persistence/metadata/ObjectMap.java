/*
 * Copyright (C) 2003-2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.myrian.persistence.metadata;

import org.myrian.persistence.common.Path;

import java.util.*;

/**
 * ObjectMap
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 **/

public class ObjectMap extends Element {


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
        if (type == null) {
            throw new IllegalArgumentException("null type");
        }
        m_type = type;
    }

    public Root getRoot() {
        Element element = this;
        while (true) {
            Object parent = element.getParent();
            if (parent == null) { return null; }
            if (parent instanceof Root) {
                return (Root) parent;
            }
            if (parent instanceof Element) {
                element = (Element) parent;
            }
        }
    }

    public ObjectMap getSuperMap() {
        if (m_type.getSupertype() == null) {
            return null;
        } else {
            return getRoot().getObjectMap(m_type.getSupertype());
        }
    }

    public ObjectMap getBaseMap() {
        if (m_type.getSupertype() == null) {
            return this;
        } else {
            return getSuperMap().getBaseMap();
        }
    }

    public ObjectType getObjectType() {
        return m_type;
    }

    public boolean isNested() {
        return getParent() instanceof Mapping;
    }

    public Mapping getContaining() {
        if (isNested()) {
            return (Mapping) getParent();
        } else {
            return null;
        }
    }

    private Mapping m_container = null;

    public Mapping getContainer() {
        if (m_container == null) {
            Object parent = getParent();
            if (parent instanceof Mapping) {
                Mapping m = (Mapping) parent;
                m_container = m.reverse(Path.get("$container"));
                addMapping(m_container);
                m_container.setMap(m.getObjectMap(), false);
            }
        }

        return m_container;
    }

    public boolean isCompound() {
        return m_type.isCompound();
    }

    public boolean isPrimitive() {
        return m_type.isPrimitive();
    }

    public boolean hasMapping(Path p) {
        return getMapping(p) != null;
    }

    public Mapping getMapping(Property p) {
        return getMapping(Path.get(p.getName()));
    }

    public Mapping getMapping(Path p) {
        Path parent = p.getParent();
        if (parent == null) {
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
        } else {
            Mapping m = getMapping(parent);
            return m.getMap().getMapping(Path.get(p.getName()));
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

    public List getColumns() {
        Object parent = getParent();
        if (parent instanceof Mapping) {
            return ((Mapping) parent).getColumns();
        } else {
            return Arrays.asList(m_table.getPrimaryKey().getColumns());
        }
    }

    public List getKeyProperties() {
        ObjectMap sm = getSuperMap();
        if (sm == null) {
            return m_key;
        } else {
            return sm.getKeyProperties();
        }
    }

    public List getKeyMappings() {
        ObjectMap sm = getSuperMap();
        if (sm == null) {
            List result = new ArrayList();
            if (isNested() && isCompound()) { result.add(getContainer()); }
            for (int i = 0; i < m_key.size(); i++) {
                result.add(getMapping((Property) m_key.get(i)));
            }
            return result;
        } else {
            return sm.getKeyMappings();
        }
    }

    public Collection getFetchedPaths() {
        return getFetchedPaths(true);
    }

    private Collection getFetchedPaths(boolean first) {
        SQLBlock sql = getRetrieveAll();
        if (first && sql != null) {
            List result = new ArrayList();
            for (Iterator it = sql.getPaths().iterator(); it.hasNext(); ) {
                Object o = it.next();
                if (!result.contains(o)) {
                    result.add(o);
                }
            }
            return result;
        } else {
            ObjectMap sm = getSuperMap();
            if (sm == null) {
                return getDeclaredFetchedPaths();
            } else {
                Collection result = sm.getFetchedPaths(false);
                result.addAll(getDeclaredFetchedPaths());
                return result;
            }
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

                public void onQualias(Qualias q) {}

                public void onNested(Nested n) {
                    Collection paths = n.getMap().getDeclaredFetchedPaths();
                    for (Iterator it = paths.iterator(); it.hasNext(); ) {
                        Path p = (Path) it.next();
                        Path fetched = Path.add(n.getPath(), p);
                        if (!result.contains(fetched)) {
                            result.add(fetched);
                        }
                    }
                }
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
        Object parent = getParent();
        if (m_table == null && parent instanceof Mapping) {
            final Table[] result = { null };
            ((Mapping) parent).dispatch(new Mapping.Switch() {
                public void onValue(Value v) {
                    result[0] = v.getColumn().getTable();
                }
                public void onStatic(Static s) {
                    result[0] = s.getObjectMap().getTable();
                }
                public void onNested(Nested n) {
                    result[0] = n.getObjectMap().getTable();
                }
                public void onJoinTo(JoinTo j) {
                    result[0] = j.getKey().getUniqueKey().getTable();
                }
                public void onJoinFrom(JoinFrom j) {
                    result[0] = j.getKey().getTable();
                }
                public void onJoinThrough(JoinThrough j) {
                    throw new Error("i dunno");
                }
                public void onQualias(Qualias q) {
                    throw new IllegalStateException();
                }
            });
            return result[0];
        } else {
            return m_table;
        }
    }

    public void setTable(Table table) {
        m_table = table;
    }

    public Collection getDeclaredTables() {
        Table t = getTable();
        return t == null ?
            Collections.EMPTY_LIST : Collections.singletonList(t);
    }

    public Collection getTables() {
        return getTables(getMappings());
    }

    private Collection getTables(Collection mappings) {
        final ArrayList result = new ArrayList();
        for (Iterator it = mappings.iterator(); it.hasNext(); ) {
            Mapping m = (Mapping) it.next();
	    Table t = m.getTable();
            if (t == null) { continue; }
            if (!result.contains(t)) {
                result.add(t);
            }
        }
        return result;
    }

    public Collection getRequiredTables() {
        Set result = new LinkedHashSet();
        addRequiredTables(this, result, new HashSet());
        return result;
    }
    private void addRequiredTables(ObjectMap om, final Set result, Set added) {
        if (added.contains(om)) { return; }
        added.add(om);
        if (m_table != null) { result.add(m_table); }
        for (Iterator it = om.getMappings().iterator(); it.hasNext(); ) {
            Mapping m = (Mapping) it.next();
            m.dispatch(new Mapping.Switch() {
                public void onValue(Value v) {
                    result.add(v.getColumn().getTable());
                }
                public void onJoinTo(JoinTo j) {
                    result.add(j.getKey().getTable());
                    result.add(j.getKey().getUniqueKey().getTable());
                }
                public void onJoinFrom(JoinFrom j) {
                    result.add(j.getKey().getTable());
                    result.add(j.getKey().getUniqueKey().getTable());
                }
                public void onJoinThrough(JoinThrough j) {
                    result.add(j.getFrom().getTable());
                    result.add(j.getFrom().getUniqueKey().getTable());
                    result.add(j.getTo().getUniqueKey().getTable());
                }
                public void onNested(Nested n) {}
                public void onQualias(Qualias q) {}
                public void onStatic(Static s) {}
            });
            addRequiredTables(m.getMap(), result, added);
        }
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

    public String toString() {
        return "object map for " + m_type + " mappings: " + m_mappings;
    }

}
