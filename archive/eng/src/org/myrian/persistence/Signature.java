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
package org.myrian.persistence;

import org.myrian.persistence.common.Path;
import org.myrian.persistence.metadata.Mapping;
import org.myrian.persistence.metadata.Model;
import org.myrian.persistence.metadata.ObjectMap;
import org.myrian.persistence.metadata.ObjectType;
import org.myrian.persistence.metadata.Property;
import org.myrian.persistence.metadata.Root;
import org.myrian.persistence.metadata.SQLBlock;
import org.myrian.persistence.oql.Expression;
import org.myrian.persistence.oql.Query;
import org.myrian.persistence.oql.Define;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Signature
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 **/

public class Signature {


    private static final Logger s_log = Logger.getLogger(Signature.class);

    private static final String s_value = "value";

    private ArrayList m_paths = new ArrayList();
    private ArrayList m_sources = new ArrayList();
    private HashMap m_sourceMap = new HashMap();

    public Signature() { }

    public Signature(Signature sig) {
        m_paths.addAll(sig.m_paths);
        m_sources.addAll(sig.m_sources);
        m_sourceMap.putAll(sig.m_sourceMap);
    }

    public Signature(ObjectType type) {
        addSource(type, null);
    }

    public Query makeQuery(Session ssn, Expression expr) {
        ObjectMap map = expr.getMap(ssn);

        // XXX: this can be called multiple times, need to make the
        // second use of m_paths here that contains the expaneded list
        // into a local variable.
        ArrayList added = new ArrayList(m_paths);
        m_paths.clear();
        for (int i = 0; i < added.size(); i++) {
            Path path = (Path) added.get(i);
            makePathLoadable(map, path);
            addDefaultProperties(map, path);
        }

        addDefaultProperties(map);

        if (m_paths.size() == 0) {
            throw new IllegalStateException(this + "\n");
        }

        if (isValue()) {
            expr = new Define(expr, s_value);
        }

        Query q = new Query(expr);

        if (isValue()) {
            q.fetch(s_value, Expression.valueOf(s_value));
            return q;
        }

        for (Iterator it = m_paths.iterator(); it.hasNext(); ) {
            Path path = (Path) it.next();
            if (path == null) { continue; }
            q.fetch(getColumn(path), Expression.valueOf(path));
        }

        return q;
    }

    private boolean isValue() {
        return m_paths.size() == 1 && m_paths.get(0) == null;
    }

    public String getColumn(Path p) {
        if (isValue() && p == null) {
            return s_value;
        }

        final int size = 30;
        final String path = p.getPath();
        if (path.length() <= size) {
            return path;
        }

        final int pathIndex = m_paths.indexOf(p);

        // compute String.valueOf(pathIndex).length() without creating a
        // throw-away String object.
        int pathIdxWidth;
        if (pathIndex == 0) {
            pathIdxWidth = 1;
        } else {
            pathIdxWidth = 0;
            for (int i = pathIndex; i > 0; i /= 10) { pathIdxWidth++; }
        }

        StringBuffer result = new StringBuffer(size);
        result.append(path.substring(0, size - pathIdxWidth));
        result.append(pathIndex);

        return result.toString();
    }

    public ObjectType getObjectType() {
        return getSource(null).getObjectType();
    }

    public void addPath(String path) {
        addPath(Path.get(path));
    }

    /**
     * Add all leaves of key property hierarchy
     */
    private void addPathImmediates(ObjectMap map, Path path) {
        ObjectMap om;
        if (path == null) {
            om = map;
        } else {
            Mapping mapping = map.getMapping(path);
            if (mapping == null) {
                throw new IllegalStateException
                    ("no mapping for '" + path + "' in " + map);
            }
            om = mapping.getMap();
        }
        List mappings = om.getKeyMappings();
        // all props for unkeyed, immediate only for keyed

        // prevent redundent fetches of container paths
        if (!isSource(path) && om.isNested() && om.isCompound()) {
            mappings = mappings.subList(1, mappings.size());
        }

        if (mappings.isEmpty()) {
            if (!m_paths.contains(path)) {
                m_paths.add(path);
            }
        } else {
            for (int i = 0; i < mappings.size(); i++) {
                Mapping m = (Mapping) mappings.get(i);
		addPathImmediates(map, Path.add(path, m.getPath()));
            }
        }
    }

    private void makePathLoadable(ObjectMap map, Path prefix,
                                  Collection paths) {
        for (Iterator it = paths.iterator(); it.hasNext(); ) {
            Path p = (Path) it.next();
            Path path;
            if (prefix == null) {
                path = p;
            } else {
                path = Path.add(prefix, p);
            }
            makePathLoadable(map, path);
        }
    }

    private void makePathLoadable(ObjectMap map, Path path) {
        addPathImmediates(map, path);
        // XXX: forcing container id properties to be loaded
        // this does not need to be done here. could push to wrapper layer
        // and change RecordSet to deal with null containers by passing
        // value to Cursor and loading in session
        if (!isSource(path)) {
            Path parent = path.getParent();
            if (!m_paths.contains(parent)) {
                makePathLoadable(map, parent);
            }
        }
    }

    public void addPath(Path path) {
	if (!exists(path)) {
	    throw new NoSuchPathException(path);
	}

        if (path == null) { return; }
        if (!m_paths.contains(path)) {
            m_paths.add(path);
        }
    }

    public Collection getPaths() {
        return m_paths;
    }

    public boolean isFetched(Path path) {
        // XXX: isFetched(null) ?
        if (path == null) { return true; }

 	for (Iterator it = getPaths().iterator(); it.hasNext(); ) {
	    Path p = (Path) it.next();
	    if (path.isAncestor(p)) {
		return true;
	    }
	}

	return false;
    }

    public void addSignature(Signature sig, Path path) {
        for (Iterator it = sig.m_sources.iterator(); it.hasNext(); ) {
            Source source = (Source) it.next();
            addSource
                (source.getObjectType(), Path.add(path, source.getPath()));
        }
        for (Iterator it = sig.m_paths.iterator(); it.hasNext(); ) {
            Path p = Path.add(path, (Path) it.next());
            if (!m_paths.contains(p)) {
                m_paths.add(p);
            }
        }
    }

    public void addSource(ObjectType type, Path path) {
        addSource(new Source(type, path));
    }

    public void addSource(Source s) {
        if (s == null) {
            throw new IllegalArgumentException
                ("Cannot add a null source.");
        }

        if (m_sourceMap.containsKey(s.getPath())) {
            throw new IllegalArgumentException
                ("Query already contains a source for that path: " +
                 s.getPath());
        }

        m_sources.add(s);
        m_sourceMap.put(s.getPath(), s);
    }

    public Source getSource(Path p) {
        if (isSource(p)) {
            return (Source) m_sourceMap.get(p);
        } else {
            return null;
        }
    }

    public boolean isSource(Path p) {
        return m_sourceMap.containsKey(p);
    }

    public Collection getSources() {
        return m_sources;
    }

    private void addPathImmediates(ObjectMap map, Path prefix,
                                   Collection paths) {
        for (Iterator it = paths.iterator(); it.hasNext(); ) {
            Path p = (Path) it.next();
            Path path;
            if (prefix == null) {
                path = p;
            } else {
                path = Path.add(prefix, p);
            }
            addPathImmediates(map, path);
        }
    }

    private void addDefaultProperties(ObjectMap map, Path path) {
        ObjectType type = getType(path);
        addFetchedPaths(map, path, type);

        if (!isSource(path)) {
            Root root = type.getRoot();
            Property prop = getProperty(path);
            // assume that path.getParent() is keyed
            ObjectMap container = root.getObjectMap(prop.getContainer());
            if (container != null) {
                makePathLoadable(map, path.getParent(),
                                 container.getDeclaredFetchedPaths());
            }
        }
    }

    private void addFetchedPaths(ObjectMap map, Path path, ObjectType type) {
        ObjectMap om = path == null ? map : map.getMapping(path).getMap();
        makePathLoadable(map, path, om.getFetchedPaths());
    }

    private void addDefaultProperties(ObjectMap map) {
        for (Iterator it = m_sources.iterator(); it.hasNext(); ) {
            Source source = (Source) it.next();
            makePathLoadable(map, source.getPath());
            addFetchedPaths(map, source.getPath(), source.getObjectType());
        }
    }

    public Property getProperty(Path path) {
        Property result = null;
        Path parent = path.getParent();
        if (isSource(parent)) {
            result = getSource(parent).getObjectType()
                .getProperty(path.getName());
        } else {
            Property prop = getProperty(parent);
            if (prop != null) {
                result = prop.getType().getProperty(path.getName());
            }
        }
        if (result == null) {
            throw new IllegalArgumentException
                ("no such property in signature: " + path);
        } else {
            return result;
        }
    }

    public ObjectType getType(Path path) {
        if (isSource(path)) {
	    return getSource(path).getObjectType();
	} else {
	    return getProperty(path).getType();
	}
    }

    public boolean exists(Path p) {
        if (isSource(p)) {
            return true;
        }

        if (p == null) {
            return false;
        }

        return exists(p.getParent()) &&
            getType(p.getParent()).getProperty(p.getName()) != null;
    }

    public String toString() {
        return "Paths are  " + m_paths + ", sources are " + m_sources;
    }

}
