package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;

import java.util.*;

/**
 * Environment
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

class Environment {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/engine/rdbms/Environment.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    private ObjectMap m_om;
    private HashMap m_values = new HashMap();
    private HashMap m_types = new HashMap();

    public Environment(ObjectMap om) {
        m_om = om;
    }

    public boolean contains(Path path) {
        return m_values.containsKey(path);
    }

    public void set(Path parameter, Object value) {
        final int type[] = { RDBMSEngine.getType(value) };

        if (m_om != null) {
            Path path = Path.get(parameter.getPath().substring(1));

            ObjectType ot = m_om.getObjectType().getType(path);
            if (ot != null) {
                type[0] = RDBMSEngine.getType(ot.getJavaClass());
            }

            Mapping m = m_om.getMapping(path);
            if (m != null) {
                m.dispatch(new Mapping.Switch() {
                    public void onValue(Value v) {
                        type[0] = v.getColumn().getType();
                    }
                    public void onJoinTo(JoinTo j) {}
                    public void onJoinFrom(JoinFrom j) {}
                    public void onJoinThrough(JoinThrough j) {}
                    public void onStatic(Static s) {}
                });
            }
        }


        set(parameter, value, type[0]);
    }

    public void set(Path parameter, Object value, int type) {
        m_values.put(parameter, value);
        m_types.put(parameter, new Integer(type));
    }

    public Object get(Path parameter) {
        return m_values.get(parameter);
    }

    public int getType(Path parameter) {
        return ((Integer) m_types.get(parameter)).intValue();
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
        super(null);
        m_base = base;
        m_path = path;
        m_splice = splice;
    }

    private Path unsplice(Path path) {
	return Path.get(":" + m_path.getRelative(path).getPath());
    }

    public boolean contains(Path path) {
        if (m_path.isAncestor(path)) {
            return m_splice.contains(unsplice(path));
        } else {
            return m_base.contains(path);
        }
    }

    public void set(Path parameter, Object value) {
        if (m_path.isAncestor(parameter)) {
	    m_splice.set(unsplice(parameter), value);
	} else {
	    m_base.set(parameter, value);
	}
    }

    public void set(Path parameter, Object value, int type) {
        if (m_path.isAncestor(parameter)) {
	    m_splice.set(unsplice(parameter), value, type);
	} else {
	    m_base.set(parameter, value, type);
	}
    }

    public Object get(Path parameter) {
        if (m_path.isAncestor(parameter)) {
            return m_splice.get(unsplice(parameter));
        } else {
            return m_base.get(parameter);
        }
    }

    public int getType(Path parameter) {
        if (m_path.isAncestor(parameter)) {
            return m_splice.getType(unsplice(parameter));
        } else {
            return m_base.getType(parameter);
        }
    }

    public String toString() {
        return "<env " + m_base + " splice on " + m_path + " with " +
            m_splice + ">";
    }

}
