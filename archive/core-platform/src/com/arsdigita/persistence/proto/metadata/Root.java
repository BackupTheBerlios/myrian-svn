package com.arsdigita.persistence.proto.metadata;

import com.arsdigita.persistence.proto.common.*;

import java.util.*;


/**
 * Root
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class Root {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/metadata/Root.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    private static final Root ROOT = new Root();

    public static final Root getRoot() {
        return ROOT;
    }

    private static final class Location {

	private final String m_filename;
	private final int m_line;
	private final int m_column;

	public Location(String filename, int line, int column) {
	    m_filename = filename;
	    m_line = line;
	    m_column = column;
	}

	public String getFilename() {
	    return m_filename;
	}

	public int getLine() {
	    return m_line;
	}

	public int getColumn() {
	    return m_column;
	}

    }

    private HashMap m_locations = new HashMap();
    private Mist m_types = new Mist(this);
    private Mist m_maps = new Mist(this);
    private Mist m_tables = new Mist(this);
    private Mist m_ops = new Mist(this);

    private Root() {}

    public void setLocation(Object element, String filename, int line,
			    int column) {
	m_locations.put(element, new Location(filename, line, column));
    }

    public boolean hasLocation(Object element) {
	return m_locations.containsKey(element);
    }

    private Location getLocation(Object element) {
	Location result = (Location) m_locations.get(element);
	if (result == null) {
	    throw new IllegalArgumentException("no such element: " + element);
	}
	return result;
    }

    public String getFilename(Object element) {
	return getLocation(element).getFilename();
    }

    public int getLine(Object element) {
	return getLocation(element).getLine();
    }

    public int getColumn(Object element) {
	return getLocation(element).getColumn();
    }

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
