package com.arsdigita.persistence.proto.common;

import java.util.*;

/**
 * Path
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/01/15 $
 **/

public class Path {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/common/Path.java#1 $ by $Author: rhs $, $DateTime: 2003/01/15 16:58:00 $";

    private static final HashMap PATHS = new HashMap();

    public static final Path get(String path) {
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
                        parent = get(path.substring(0, dot));
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

}
