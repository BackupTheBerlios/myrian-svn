package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import org.apache.log4j.Logger;

import java.util.*;
import java.sql.*;

/**
 * StaticEventSwitch
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/02/26 $
 **/

class StaticEventSwitch extends Event.Switch {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/StaticEventSwitch.java#2 $ by $Author: rhs $, $DateTime: 2003/02/26 12:01:31 $";

    private static final Logger LOG =
        Logger.getLogger(StaticEventSwitch.class);

    private RDBMSEngine m_engine;

    StaticEventSwitch(RDBMSEngine engine) {
        m_engine = engine;
    }

    private void addOperations(Object obj, Collection blocks) {
        ObjectType type = Session.getObjectType(obj);
        m_engine.addOperations(obj);
        m_engine.clearOperations(obj);
        for (Iterator it = blocks.iterator(); it.hasNext(); ) {
            SQLBlock block = (SQLBlock) it.next();
            if (LOG.isDebugEnabled()) {
                LOG.debug("adding block: " + block);
            }
            Environment env = m_engine.getEnvironment(obj);
            StaticOperation op = new StaticOperation(block, env);
            set(env, type, obj, null);
            m_engine.addOperation(obj, op);
        }
    }

    public void onCreate(CreateEvent e) {
        addOperations(e.getObject(), e.getObjectMap().getInserts());
    }

    public void onDelete(DeleteEvent e) {
        addOperations(e.getObject(), e.getObjectMap().getDeletes());
    }

    public void onSet(SetEvent e) {
        Object obj = e.getObject();
        Collection ops = m_engine.getOperations(obj);
        if (ops == null) {
            addOperations(obj, e.getObjectMap().getUpdates());
            ops = m_engine.getOperations(obj);
        }

        Property prop = e.getProperty();
        Path path = Path.get(prop.getName());

        Environment env = m_engine.getEnvironment(obj);
        set(env, prop.getType(), e.getArgument(), path);
    }

    private void addOperations(Object from, Property prop, Object to,
                               Collection blocks) {
        Environment fromEnv = m_engine.getEnvironment(from);
        set(fromEnv, prop.getContainer(), from, null);
        Environment toEnv = m_engine.getEnvironment(to);
        set(toEnv, prop.getType(), to, null);

        Path path = Path.get(prop.getName());
        Environment env = new SpliceEnvironment(fromEnv, path, toEnv);
        Role role = (Role) prop;
        if (role.isReversable()) {
            env = new SpliceEnvironment
                (env, Path.get(role.getReverse().getName()), fromEnv);
        }

        for (Iterator it = blocks.iterator(); it.hasNext(); ) {
            SQLBlock block = (SQLBlock) it.next();
            StaticOperation op = new StaticOperation(block, env);
            m_engine.addOperation(op);
        }
    }

    private static final class SpliceEnvironment extends Environment {
        private Environment m_base;
        private Path m_path;
        private Environment m_splice;

        public SpliceEnvironment(Environment base, Path path,
                                 Environment splice) {
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

    private void set(Environment env, ObjectType type, Object obj,
                     Path path) {
        if (!type.hasKey()) {
            Path p = Path.get(path.getPath());
            env.set(p, obj);
            return;
        }

        PropertyMap props;
        if (obj == null) {
            props = new PropertyMap();
        } else {
            props = Session.getProperties(obj);
        }

        for (Iterator it = type.getKeyProperties().iterator();
             it.hasNext(); ) {
            Property key = (Property) it.next();

            Path keyPath;
            if (path == null) {
                keyPath = Path.get(key.getName());
            } else {
                keyPath = Path.get(path.getPath() + "." + key.getName());
            }

            set(env, key.getType(), props.get(key), keyPath);
        }
    }

    public void onAdd(AddEvent e) {
        Property prop = e.getProperty();
        Mapping m = e.getObjectMap().getMapping(Path.get(prop.getName()));
        addOperations(e.getObject(), prop, e.getArgument(), m.getAdds());
    }

    public void onRemove(RemoveEvent e) {
        Property prop = e.getProperty();
        Mapping m = e.getObjectMap().getMapping(Path.get(prop.getName()));
        addOperations(e.getObject(), prop, e.getArgument(), m.getRemoves());
    }

}
