/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.JoinFrom;
import com.redhat.persistence.metadata.JoinThrough;
import com.redhat.persistence.metadata.JoinTo;
import com.redhat.persistence.metadata.Mapping;
import com.redhat.persistence.metadata.ObjectMap;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Static;
import com.redhat.persistence.metadata.Value;
import com.redhat.persistence.metadata.Qualias;
import java.util.HashMap;

/**
 * Environment
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/03/30 $
 **/

class Environment {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/engine/rdbms/Environment.java#6 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

    private RDBMSEngine m_engine;
    private ObjectMap m_om;
    private HashMap m_values = new HashMap();
    private HashMap m_types = new HashMap();

    public Environment(RDBMSEngine engine, ObjectMap om) {
        m_engine = engine;
        m_om = om;
    }

    RDBMSEngine getEngine() {
        return m_engine;
    }

    public boolean contains(Path path) {
        return m_values.containsKey(path);
    }

    public void set(Path parameter, Object value) {
        final int type[] = {
            RDBMSEngine.getType(m_engine.getSession().getRoot(), value)
        };

        if (m_om != null) {
            Path path = Path.get(parameter.getPath().substring(1));

            ObjectType ot = m_om.getObjectType().getType(path);
            if (ot != null) {
                type[0] = RDBMSEngine.getType
                    (m_om.getRoot(), ot.getJavaClass());
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
                    public void onQualias(Qualias q) {
                        // XXX do real read only properties from session
                    }
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
        super(base.getEngine(), null);
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
