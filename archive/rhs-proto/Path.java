package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.metadata.*;
import com.arsdigita.persistence.OID;
import java.util.*;

/**
 * Path
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/11/27 $
 **/

public class Path {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/Path.java#2 $ by $Author: rhs $, $DateTime: 2002/11/27 17:41:53 $";

    private static final HashMap PATHS = new HashMap();

    public static final Path getInstance(String path) {
        if (path == null) {
            return null;
        }

        Path result;
        
        if (PATHS.containsKey(path)) {
            result = (Path) PATHS.get(path);
        } else {
            synchronized (PATHS) {
                if (PATHS.containsKey(path)) {
                    result = (Path) PATHS.get(path);
                } else {
                    int dot = path.lastIndexOf('.');
                    Path parent;
                    String name;
                    if (dot > -1) {
                        parent = getInstance(path.substring(0, dot));
                        name = path.substring(dot + 1);
                    } else {
                        parent = null;
                        name = path;
                    }

                    result = new Path(parent, name);
                    PATHS.put(path, result);
                }
            }
        }

        return result;
    }

    private Path m_parent;
    private String m_name;

    private Path(Path parent, String name) {
        m_parent = parent;
        m_name = name;
    }

    public Path getParent() {
        return m_parent;
    }

    public String getName() {
        return m_name;
    }

    public String getPath() {
        if (m_parent == null) {
            return m_name;
        } else {
            return m_parent + "." + m_name;
        }
    }

    public String toString() {
        return getPath();
    }

    public Object get(Session ssn, OID start) {
        if (m_parent == null) {
            return ssn.get(start, start.getObjectType().getProperty(m_name));
        } else {
            Object value = m_parent.get(ssn, start);
            if (value instanceof PersistentObject) {
                OID oid = ((PersistentObject) value).getOID();
                return ssn.get(oid, oid.getObjectType().getProperty(m_name));
            } else {
                throw new IllegalArgumentException
                    ("Path refers to attribute of opaque type: " + this);
            }
        }
    }

    public Property getProperty(ObjectType start) {
        if (m_parent == null) {
            return start.getProperty(m_name);
        } else {
            try {
                Property prop = m_parent.getProperty(start);
                ObjectType type = (ObjectType) prop.getType();
                return type.getProperty(m_name);
            } catch (ClassCastException e) {
                throw new IllegalArgumentException
                    ("Path refers to attribute of opaque type: " + this);
            }
        }
    }

    public boolean isKey(ObjectType type) {
        return getProperty(type).isKeyProperty();
    }

    public ObjectType getType(ObjectType start) {
        return (ObjectType) getProperty(start).getType();
    }

}
