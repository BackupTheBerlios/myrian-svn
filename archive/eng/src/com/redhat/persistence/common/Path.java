/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.redhat.persistence.common;

import com.arsdigita.util.ConcurrentDict;

/**
 * Path
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class Path {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/common/Path.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
