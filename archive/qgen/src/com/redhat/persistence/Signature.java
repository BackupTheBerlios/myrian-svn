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

package com.redhat.persistence;

import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.Model;
import com.redhat.persistence.metadata.ObjectMap;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Property;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.metadata.SQLBlock;
import com.redhat.persistence.oql.Expression;
import com.redhat.persistence.oql.Get;
import com.redhat.persistence.oql.Variable;
import com.redhat.persistence.oql.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * Signature
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #6 $ $Date: 2004/03/03 $
 **/

public class Signature {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/Signature.java#6 $ by $Author: ashah $, $DateTime: 2004/03/03 18:10:03 $";

    private static final Logger s_log = Logger.getLogger(Signature.class);

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

    // XXX: should be public static in oql
    private static Expression expression(Path path) {
        if (path.getParent() == null) {
            return new Variable(path.getName());
        } else {
            return new Get(expression(path.getParent()), path.getName());
        }
    }

    private String clean(String str) {
        // XXX: this is not legit we could get name conflicts
        str = str.replace('.', '_');
        str = str.replace('@', '_');
        if (str.length() > 28) {
            str = str.substring(0, 28);
        }
        return str;
    }

    public Query makeQuery(Expression expr) {
        addDefaultProperties();

        if (m_paths.size() == 0) {
            throw new IllegalStateException(this + "\n");
        }

        Query q = new Query(expr);

        for (Iterator it = m_paths.iterator(); it.hasNext(); ) {
            Path path = (Path) it.next();
            if (path == null) { continue; }
            q.fetch(getColumn(path), expression(path));
        }

        return q;
    }

    public String getColumn(Path p) {
        return clean(p.getPath()) + m_paths.indexOf(p);
    }

    public ObjectType getObjectType() {
        return getSource(null).getObjectType();
    }

    public boolean hasPath(Path p) {
        return m_paths.contains(p);
    }

    public void addPath(String path) {
        addPath(Path.get(path));
    }

    /**
     * Add all leaves of key property hierarchy
     */
    private void addPathImmediates(Path path) {
	ObjectType type = getType(path);
        Collection props = type.getImmediateProperties();
        // all props for unkeyed, immediate only for keyed

        if (props.size() == 0 && !isSource(path)) {
            if (!m_paths.contains(path)) {
                m_paths.add(path);
            }
        } else {
            for (Iterator it = props.iterator(); it.hasNext(); ) {
                Property prop = (Property) it.next();
		addPathImmediates(Path.add(path, prop.getName()));
            }
        }
    }

    private void makePathLoadable(Path path) {
        addPathImmediates(path);
        // XXX: forcing container id properties to be loaded
        // this does not need to be done here. could push to wrapper layer
        // and change RecordSet to deal with null containers by passing
        // value to Cursor and loading in session
        if (!isSource(path)) {
            Path parent = path.getParent();
            if (!m_paths.contains(parent)) {
                makePathLoadable(parent);
            }
        }
    }

    public void addPath(Path path) {
	if (!exists(path)) {
	    throw new NoSuchPathException(path);
	}

        if (path == null) { return; }

        makePathLoadable(path);
        addDefaultProperties(path);
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
            m_paths.add(Path.add(path, (Path) it.next()));
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

    private void addPathImmediates(Path prefix, Collection paths) {
        for (Iterator it = paths.iterator(); it.hasNext(); ) {
            Path p = (Path) it.next();
            Path path;
            if (prefix == null) {
                path = p;
            } else {
                path = Path.add(prefix, p);
            }
            addPathImmediates(path);
        }
    }

    private void addDefaultProperties(Path path) {
        ObjectType type = getType(path);
        addFetchedPaths(path, type);

        if (!isSource(path)) {
            Root root = type.getRoot();
            Property prop = getProperty(path);
            // assume that path.getParent() is keyed
            ObjectMap container = root.getObjectMap(prop.getContainer());
            if (container != null) {
                addPathImmediates
                    (path.getParent(), container.getDeclaredFetchedPaths());
            }
        }
    }

    private void addFetchedPaths(Path path, ObjectType type) {
        Root root = type.getRoot();
        if (root == null) { return; }
        ObjectMap om = root.getObjectMap(type);
        addPathImmediates(path, om.getFetchedPaths());
    }

    private void addDefaultProperties() {
        for (Iterator it = m_sources.iterator(); it.hasNext(); ) {
            Source source = (Source) it.next();
            makePathLoadable(source.getPath());
            addFetchedPaths(source.getPath(), source.getObjectType());
        }
    }

    public Property getProperty(Path path) {
        Path parent = path.getParent();
        if (isSource(parent)) {
            return getSource(parent).getObjectType().getProperty
                (path.getName());
        } else {
            Property prop = getProperty(parent);
            if (prop == null) {
                throw new IllegalArgumentException
                    ("no such property in signature: " + path);
            } else {
                return prop.getType().getProperty(path.getName());
            }
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
	} else {
	    return exists(p.getParent()) &&
		getType(p.getParent()).getProperty(p.getName()) != null;
	}
    }

    public String toString() {
        return "Paths are  " + m_paths + ", sources are " + m_sources;
    }
}
