/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.redhat.persistence.metadata;

import com.redhat.persistence.common.*;

import java.util.*;
import java.io.*;


/**
 * Root
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2003/09/17 $
 **/

public class Root {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/metadata/Root.java#6 $ by $Author: rhs $, $DateTime: 2003/09/17 15:39:02 $";

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
    private Map m_adapters = new HashMap();

    public Root() {}

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

    public void addAdapter(Class javaClass, Adapter ad) {
        m_adapters.put(javaClass, ad);
        ad.setRoot(this);
    }

    public Adapter getAdapter(Class javaClass) {
        for (Class c = javaClass; c != null; c = c.getSuperclass()) {
            Adapter a = (Adapter) m_adapters.get(c);
            if (a != null) { return a; }
        }

        return null;
    }

    public Adapter getAdapter(ObjectType type) {
        for (ObjectType ot = type; ot != null; ot = ot.getSupertype()) {
	    Class klass = ot.getJavaClass();
	    if (klass != null) {
		Adapter a = getAdapter(klass);
		if (a != null) { return a; }
	    }
        }

        Adapter a = (Adapter) m_adapters.get(null);
        if (a != null) { return a; }

        return null;
    }

    public void dump(PrintStream out) {
        out.println("types:");
        dump(out, m_types);
        out.println("maps:");
        dump(out, m_maps);
        out.println("tables:");
        dump(out, m_tables);
        out.println("ops:");
        dump(out, m_ops);
        out.println("adapters:");
        dump(out, m_adapters);
    }

    private void dump(PrintStream out, Collection c) {
        for (Iterator it = c.iterator(); it.hasNext(); ) {
            out.println("    " + it.next());
        }
    }

    private void dump(PrintStream out, Map m) {
        for (Iterator it = m.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            out.println("    " + me.getKey() + ": " + me.getValue());
        }
    }

}
