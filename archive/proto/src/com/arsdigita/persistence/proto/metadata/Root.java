package com.arsdigita.persistence.proto.metadata;

import com.arsdigita.persistence.proto.common.*;

import java.util.*;


/**
 * Root
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2003/03/27 $
 **/

public class Root {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/Root.java#5 $ by $Author: rhs $, $DateTime: 2003/03/27 15:13:02 $";

    private static final Root ROOT = new Root();

    public static final Root getRoot() {
        return ROOT;
    }

    private Mist m_types = new Mist(this);
    private Mist m_maps = new Mist(this);
    private Mist m_tables = new Mist(this);
    private Mist m_ops = new Mist(this);

    private Root() {}

    public boolean hasObjectType(String qualifiedName) {
        return m_types.containsKey(qualifiedName);
    }

    public void addObjectType(ObjectType type) {
        m_types.add(type);
    }

    public ObjectType getObjectType(String qualifiedName) {
        return (ObjectType) m_types.get(qualifiedName);
    }

    public Collection getObjectTypes() {
        return m_types;
    }

    public ObjectMap getObjectMap(ObjectType type) {
        return (ObjectMap) m_maps.get(type);
    }

    public void addObjectMap(ObjectMap map) {
        m_maps.add(map);
    }

    public Collection getObjectMaps() {
	return m_maps;
    }

    public boolean hasTable(String name) {
        return m_tables.containsKey(name);
    }

    public Table getTable(String name) {
        return (Table) m_tables.get(name);
    }

    public void addTable(Table table) {
        m_tables.add(table);
    }

    public Collection getTables() {
        return m_tables;
    }

    public Collection getDataOperations() {
        return m_ops;
    }

    public void addDataOperation(DataOperation op) {
        m_ops.add(op);
    }

    public DataOperation getDataOperation(Path name) {
        return (DataOperation) m_ops.get(name);
    }

}
