package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.common.*;

import java.util.*;

/**
 * Environment
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/03/05 $
 **/

class Environment {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Environment.java#2 $ by $Author: rhs $, $DateTime: 2003/03/05 18:41:57 $";

    private HashMap m_values = new HashMap();

    public boolean isParameter(Path path) {
        return m_values.containsKey(path);
    }

    public void set(Path parameter, Object value) {
        m_values.put(parameter, value);
    }

    public Object get(Path parameter) {
        return m_values.get(parameter);
    }

    public String toString() {
        return m_values.toString();
    }

}

class SpliceEnvironment extends Environment {

    private Environment m_base;
    private Path m_path;
    private Environment m_splice;

    public SpliceEnvironment(Environment base, Path path, Environment splice) {
        m_base = base;
        m_path = path;
        m_splice = splice;
    }

    public boolean isParameter(Path path) {
        if (m_path.isAncestor(path)) {
            return m_splice.isParameter(m_path.getRelative(path));
        } else {
            return m_base.isParameter(path);
        }
    }

    public void set(Path parameter, Object value) {
        throw new UnsupportedOperationException();
    }

    public Object get(Path parameter) {
        if (m_path.isAncestor(parameter)) {
            return m_splice.get(m_path.getRelative(parameter));
        } else {
            return m_base.get(parameter);
        }
    }

    public String toString() {
        return "<env " + m_base + " splice on " + m_path + " with " +
            m_splice + ">";
    }

}
