package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;
import java.util.*;

/**
 * Signature
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #20 $ $Date: 2003/04/28 $
 **/

public class Signature {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Signature.java#20 $ by $Author: ashah $, $DateTime: 2003/04/28 16:45:07 $";

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

    public void addPath(Path path) {
	if (!exists(path)) {
	    throw new NoSuchPathException(path);
	}

	ObjectType type = getType(path);
	Collection keys = type.getKeyProperties();
	if (keys.size() == 0) {
	    if (!m_paths.contains(path)) {
		m_paths.add(path);
	    }
	} else {
	    for (Iterator it = keys.iterator(); it.hasNext(); ) {
		Property prop = (Property) it.next();
		addPath(Path.add(path, prop.getName()));
	    }
	}
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
        ObjectType type;
        String prefix;

        if (path == null) {
            type = getObjectType();
            prefix = "";
        } else {
            type = getObjectType().getType(path);
            prefix = path.getPath() + ".";
        }

        for (Iterator it = props.iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            addPath(prefix + prop.getName());
        }
        // should add aggressively loaded properties
    }

    private Collection getAttributeProperties(Collection properties) {
        ArrayList result = new ArrayList(properties.size());
        for (Iterator it = properties.iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            if (isAttribute(prop)) { result.add(prop); }
        }
        return result;
    }

    public void addDefaultProperties(Path path) {
        addProperties(path, getAttributeProperties
                      (getObjectType().getType(path).getProperties()));
    }

    public void addDefaultProperties() {
        addProperties(getAttributeProperties(getObjectType().getProperties()));
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
            return getProperty(parent).getType().getProperty(path.getName());
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
