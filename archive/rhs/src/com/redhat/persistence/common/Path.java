/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.redhat.persistence.common;

import com.arsdigita.util.ConcurrentDict;

/**
 * Path
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2004/05/02 $
 **/

public class Path {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/common/Path.java#3 $ by $Author: rhs $, $DateTime: 2004/05/02 13:12:27 $";

    //special case the id path since it shows up so often
    private static final Path ID_PATH = new Path("id");
    private static final int NO_DOT = -1;

    private static final ConcurrentDict DICT =
        new ConcurrentDict(new Supplier());

    private Path m_parent;  // initialized lazily from m_path
    private final String m_path;
    private final int m_lastDot;

    private Path(String path) {
        m_path = path;
        m_lastDot = m_path.lastIndexOf('.');
    }

    public static final Path get(String path) {
        if ("id".equals(path)) {
            return ID_PATH;
        }
        return (Path) DICT.get(path);
    }

    public static final Path add(String p1, String p2) {
        return Path.get(concat(p1, p2));
    }

    public static final Path add(Path p1, String p2) {
        return p1==null ? Path.get(p2) : Path.get(concat(p1.m_path, p2));
    }

    public static final Path add(String p1, Path p2) {
        return p2==null ? Path.get(p1): Path.get(concat(p1, p2.m_path));
    }

    public static final Path add(Path p1, Path p2) {
        if (p1 == null) {
            return p2;
        } else if (p2 == null) {
            return p1;
        } else {
            return Path.get(concat(p1.m_path, p2.m_path));
        }
    }

    public static final Path relative(Path base, Path descendent) {
        if (base == null) {
            return descendent;
        } else {
            return base.getRelative(descendent);
        }
    }

    public Path getParent() {
        if ( m_lastDot == NO_DOT ) {
            return null;
        }

        synchronized(this) {
            if (m_parent == null ) {
                m_parent = Path.get(m_path.substring(0, m_lastDot));
            }
            return m_parent;
        }
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
        return m_path.substring(m_lastDot+1);
    }

    public String getPath() {
        return m_path;
    }

    public String toString() {
        return m_path;
    }

    private static String concat(String s1, String s2) {
        if (s1 == null ) {
            return s2;
        } else if (s2 == null ) {
            return s1;
        } else {
            StringBuffer sb = new StringBuffer(s1.length() + s2.length() + 1);
            sb.append(s1).append(".").append(s2);
            return sb.toString();
        }
    }

    private static class Supplier implements ConcurrentDict.EntrySupplier {
        public Object supply(Object key) {
            return new Path((String) key);
        }
    }
}
