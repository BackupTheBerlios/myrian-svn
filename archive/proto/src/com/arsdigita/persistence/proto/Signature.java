package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.*;
import java.util.*;

/**
 * Signature
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2003/01/06 $
 **/

public class Signature {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Signature.java#5 $ by $Author: rhs $, $DateTime: 2003/01/06 17:58:56 $";

    private ObjectType m_type;
    private HashMap m_paths = new HashMap();

    private ArrayList m_sources = new ArrayList();
    private HashMap m_sourceMap = new HashMap();

    private ArrayList m_parameters = new ArrayList();
    private HashMap m_parameterMap = new HashMap();

    public Signature(ObjectType type) {
        m_type = type;
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
