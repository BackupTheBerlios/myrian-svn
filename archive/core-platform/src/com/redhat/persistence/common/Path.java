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

import com.arsdigita.util.ConcurrentDict;

/**
 * Path
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #8 $ $Date: 2004/02/20 $
 **/

public class Path {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/common/Path.java#8 $ by $Author: vadim $, $DateTime: 2004/02/20 12:36:50 $";

    //special case the id path since it shows up so often
    private static final Path ID_PATH = new Path(null, "id");

    private static final ConcurrentDict DICT =
        new ConcurrentDict(new Supplier());

    public static final Path get(String path) {
        if ("id".equals(path)) {
            return ID_PATH;
        }
        return (Path) DICT.get(path);
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

    private static class Supplier implements ConcurrentDict.EntrySupplier {
        public Object supply(Object key) {
            String path = (String) key;

            final int dot = path.lastIndexOf('.');
            final Path parent;
            final String name;
            if (dot > -1) {
                parent = get(path.substring(0, dot));
                name = path.substring(dot + 1);
            } else {
                parent = null;
                name = path;
            }

            return new Path(parent, name);
        }
    }
}
