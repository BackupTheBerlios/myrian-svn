package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.*;
import java.util.*;

/**
 * Signature
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #8 $ $Date: 2003/01/10 $
 **/

public class Signature {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Signature.java#8 $ by $Author: rhs $, $DateTime: 2003/01/10 17:10:28 $";

    private ObjectType m_type;
    private HashMap m_paths = new HashMap();

    private ArrayList m_sources = new ArrayList();
    private HashMap m_sourceMap = new HashMap();

    private ArrayList m_parameters = new ArrayList();
    private HashMap m_parameterMap = new HashMap();

    public Signature(ObjectType type) {
        m_type = type;
        addKeyProperties();
    }

    public ObjectType getObjectType() {
        return m_type;
    }

    public void addPath(String path) {
        if (!m_paths.containsKey(path)) {
            m_paths.put(path, Path.getInstance(path));
        }
    }

    Path getPath(String path) {
        return (Path) m_paths.get(path);
    }

    public Collection getPaths() {
        return m_paths.values();
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

    public Parameter getParameter(Path p) {
        return (Parameter) m_parameterMap.get(p);
    }

    public Collection getParameters() {
        return m_parameters;
    }

    static final boolean isAttribute(Property prop) {
        // This should really look at the mapping metadata to figure out what
        // to load by default.
        return prop.getType().getModel().equals(Model.getInstance("global"));
    }

    public void addProperties(Collection props) {
        addProperties(null, props);
    }

    public void addProperties(Path path, Collection props) {
        ObjectType type;
        String prefix;

        if (path == null) {
            type = m_type;
            prefix = "";
        } else {
            type = path.getType(m_type);
            prefix = path.getPath() + ".";
        }

        for (Iterator it = props.iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            if (isAttribute(prop)) {
                addPath(prefix + prop.getName());
            }
        }
        // should add aggressively loaded properties
    }

    public void addDefaultProperties(Path path) {
        addProperties(path, path.getType(m_type).getProperties());
    }

    public void addDefaultProperties() {
        addProperties(m_type.getProperties());
    }

    private void addKeyProperties() {
        Collection props = m_type.getRoot().getObjectMap(m_type)
            .getKeyProperties();
        addProperties(props);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(m_type.getQualifiedName() + "(");

        for (Iterator it = m_paths.values().iterator(); it.hasNext(); ) {
            buf.append(it.next());
            if (it.hasNext()) {
                buf.append(", ");
            }
        }

        buf.append(")");

        return buf.toString();
    }

}
