/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.redhat.persistence.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Path
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2004/02/19 $
 **/

public class Path {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/common/Path.java#5 $ by $Author: bche $, $DateTime: 2004/02/19 11:07:55 $";

    private static final HashMap MAPS = new HashMap();

    public static final Path get(String path) {
        if (path == null) {
            return null;
        }

        final Map map;

        final String pathPrefix = getPrefix(path);
        //pathPrefix is something like "com.arsdigita.foo" or
        //"com.arsdigita.bar" now.
        
        // this lock is held for a relatively short period of time
        synchronized (MAPS) {                                    
            if (MAPS.containsKey(pathPrefix) ) {
                map = (Map) MAPS.get(pathPrefix);
            } else {
                map = new HashMap();
                MAPS.put(pathPrefix, map);
            }
        }
        
        // we're done with the lock on MAPS at this point
        
        synchronized (map) {
            if (map.containsKey(path)) {
                return (Path) map.get(path);
            } else {
                Path parent;
                String name;
                
                if (pathPrefix == null) {
                    parent = null;
                    name = path;
                } else {
                    parent = get(pathPrefix);
                    name = path.substring(pathPrefix.length()+1, path.length());
                }

                Path result = new Path(parent, name);
                map.put(path, result);
                return result;
            }
        }
    }

    private static String getPrefix(String path) {
        int dot = path.lastIndexOf('.');
        if (dot > -1) {
            return path.substring(0, dot);
        } else {
            return null;
        }
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
            return Path.get(concat(p1.getPath(),p2.getPath()));
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
                return concat(parent, path.getName());
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
            return concat(m_parent.getPath(), m_name);
        }
    }

    public String toString() {
        return getPath();
    }


    // both params are guaranteed to be non-null
    private static String concat(String s1, String s2) {
        StringBuffer sb = new StringBuffer(s1.length() + s2.length() + 1);
        sb.append(s1).append(".").append(s2);
        return sb.toString();
    }
}
