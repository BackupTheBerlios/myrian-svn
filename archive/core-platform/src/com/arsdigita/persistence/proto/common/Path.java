package com.arsdigita.persistence.proto.common;

import java.util.*;

/**
 * Path
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class Path {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/common/Path.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    private static final HashMap PATHS = new HashMap();

    public static final Path get(String path) {
        if (path == null) {
            return null;
        }

        Path result;

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

        return result;
    }

    public static final Path add(String p1, String p2) {
	return Path.add(Path.get(p1), Path.get(p2));
    }

    public static final Path add(Path p1, String p2) {
        return Path.add(p1, Path.get(p2));
    }

    public static final Path add(String p1, Path p2) {
        return Path.add(Path.get(p1), p2);
    }

    public static final Path add(Path p1, Path p2) {
        if (p1 == null) {
            return p2;
        } else if (p2 == null) {
            return p1;
        } else {
            return Path.get(p1.getPath() + "." + p2.getPath());
        }
    }

    public static final Path relative(Path base, Path descendent) {
        if (base == null) {
            return descendent;
        } else {
            return base.getRelative(descendent);
        }
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

    public boolean isAncestor(Path path) {
        if (path == null) {
            return false;
        } else if (this.equals(path)) {
            return true;
        } else {
            return isAncestor(path.getParent());
        }
    }

    private String getRelativeString(Path path) {
        if (path == null) {
            throw new Error("not a child path");
        } else if (this.equals(path)) {
            return null;
        } else {
            String parent = getRelativeString(path.getParent());
            if (parent == null) {
                return path.getName();
            } else {
                return parent + "." + path.getName();
            }
        }
    }

    public Path getRelative(Path path) {
        return Path.get(getRelativeString(path));
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
