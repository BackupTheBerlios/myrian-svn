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

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Signature
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2003/09/03 $
 **/

public class Signature {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/Signature.java#4 $ by $Author: justin $, $DateTime: 2003/09/03 12:09:13 $";

    private ArrayList m_paths = new ArrayList();

    private ArrayList m_sources = new ArrayList();
    private HashMap m_sourceMap = new HashMap();

    private ArrayList m_parameters = new ArrayList();
    private HashMap m_parameterMap = new HashMap();

    public Signature(Source src) {
        addSource(src);
        addKeyProperties();
    }

    public Signature(ObjectType type) {
        this(new Source(type));
    }

    public Signature(Signature sig) {
	m_paths.addAll(sig.m_paths);
	m_sources.addAll(sig.m_sources);
	m_sourceMap.putAll(sig.m_sourceMap);
	m_parameters.addAll(sig.m_parameters);
	m_parameterMap.putAll(sig.m_parameterMap);
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
    private void addPathKeys(Path path) {
	ObjectType type = getType(path);
	Collection keys = type.getKeyProperties();
	if (keys.size() == 0) {
            if (!m_paths.contains(path)) {
                m_paths.add(path);
            }
        } else {
            for (Iterator it = keys.iterator(); it.hasNext(); ) {
                Property prop = (Property) it.next();
		addPathKeys(Path.add(path, prop.getName()));
            }
        }
    }

    public void addPath(Path path) {
	if (!exists(path)) {
	    throw new NoSuchPathException(path);
	}
        if (path == null) { return; }
        addPathKeys(path);
        // make sure its container id properties are loaded
        addPath(path.getParent());
    }

    Path getPath(String path) {
        Path p = Path.get(path);
        if (m_paths.contains(p)) {
            return p;
        } else {
            return null;
        }
    }

    public Collection getPaths() {
        return m_paths;
    }

    public boolean isFetched(Path path) {
	for (Iterator it = getPaths().iterator(); it.hasNext(); ) {
	    Path p = (Path) it.next();
	    if (path.isAncestor(p)) {
		return true;
	    }
	}

	return false;
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

        if (m_parameterMap.containsKey(s.getPath())) {
            throw new IllegalArgumentException
                ("Query contains a parameter with that path: " +
                 s.getPath());
        }

        m_sources.add(s);
        m_sourceMap.put(s.getPath(), s);
    }

    public Source getSource(Path p) {
        return (Source) m_sourceMap.get(p);
    }

    public boolean isSource(Path p) {
        return m_sourceMap.containsKey(p);
    }

    public Collection getSources() {
        return m_sources;
    }

    public void addParameter(Parameter p) {
        if (p == null) {
            throw new IllegalArgumentException
                ("Cannot add a null parameter.");
        }

        if (m_parameterMap.containsKey(p.getPath())) {
            throw new IllegalArgumentException
                ("Query already contains a parameter for that path: " +
                 p.getPath());
        }

        if (m_sourceMap.containsKey(p.getPath())) {
            throw new IllegalArgumentException
                ("Query contains a source with that path: " + p.getPath());
        }

        m_parameters.add(p);
        m_parameterMap.put(p.getPath(), p);
    }

    public boolean isParameter(Path p) {
        return m_parameterMap.containsKey(p);
    }

    public Parameter getParameter(Path p) {
        return (Parameter) m_parameterMap.get(p);
    }

    public Collection getParameters() {
        return m_parameters;
    }

    static final boolean isAttribute(Property prop) {
        // This should really look at the mapping metadata to figure out what
        // to load by default.
        return !prop.isCollection() &&
            prop.getType().getModel().equals(Model.getInstance("global"));
    }

    private void addProperties(Collection props) {
        addProperties(null, props);
    }

    private void addProperties(Path path, Collection props) {
        ArrayList paths = new ArrayList(props.size());
        for (Iterator it = props.iterator(); it.hasNext(); ) {
            paths.add(Path.get(((Property) it.next()).getName()));
        }
        addPaths(path, paths);
    }

    private void addPaths(Collection paths) {
        addPaths(null, paths);
    }

    private void addPaths(Path path, Collection paths) {
        ObjectType type;
        String prefix;

        if (path == null) {
            type = getObjectType();
            prefix = "";
        } else {
            type = getObjectType().getType(path);
            prefix = path.getPath() + ".";
        }

        for (Iterator it = paths.iterator(); it.hasNext(); ) {
            Path p = (Path) it.next();
            addPath(prefix + p.getPath());
        }
    }

    public void addDefaultProperties(Path path) {
        ObjectType type = getObjectType().getType(path);
        Root root = type.getRoot();
        if (type.isKeyed()) {
            addPaths(path, root.getObjectMap(type).getFetchedPaths());
        } else {
            Property prop = getObjectType().getProperty(path);
            // assume that path.getParent() is keyed
            ObjectMap container = root.getObjectMap(prop.getContainer());
            addPaths(path.getParent(), container.getDeclaredFetchedPaths());
        }
    }

    public void addDefaultProperties() {
        ObjectType type = getObjectType();
        Root root = type.getRoot();
        addPaths(root.getObjectMap(type).getFetchedPaths());
    }

    private void addKeyProperties() {
        addProperties(getObjectType().getKeyProperties());
    }

    public boolean isImmediate(Path path) {
	Property prop = getProperty(path);
	Collection keys = prop.getContainer().getKeyProperties();
	return keys.size() == 0 || keys.contains(prop);
    }

    public Property getProperty(Path path) {
        Path parent = path.getParent();
        if (isParameter(parent)) {
            return getParameter(parent).getObjectType().getProperty
                (path.getName());
        } else if (isSource(parent)) {
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
	if (isParameter(path)) {
	    return getParameter(path).getObjectType();
	} else if (isSource(path)) {
	    return getSource(path).getObjectType();
	} else {
	    return getProperty(path).getType();
	}
    }

    public boolean exists(Path p) {
	if (isParameter(p)) {
	    return true;
	} else if (isSource(p)) {
	    return true;
	} else {
	    return exists(p.getParent()) &&
		getType(p.getParent()).getProperty(p.getName()) != null;
	}
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(getObjectType().getQualifiedName() + "(");

        for (Iterator it = m_paths.iterator(); it.hasNext(); ) {
            buf.append(it.next());
            if (it.hasNext()) {
                buf.append(", ");
            }
        }

        buf.append(")");

        return buf.toString();
    }

}
