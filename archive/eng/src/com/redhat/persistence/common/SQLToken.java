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

/**
 * SQLToken
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class SQLToken {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/common/SQLToken.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    public static class Type {

        private String m_name;

        private Type(String name) {
            m_name = name;
        }

        public String toString() {
            return m_name;
        }

    }

    public static final Type BIND = new Type("BIND");
    public static final Type PATH = new Type("PATH");
    public static final Type RAW = new Type("RAW");
    public static final Type SPACE = new Type("SPACE");

    SQLToken m_previous = null;
    SQLToken m_next = null;
    private String m_image;
    private Type m_type;

    public SQLToken(String image, Type type) {
        m_image = image;
        m_type = type;
    }

    public SQLToken getPrevious() {
        return m_previous;
    }

    public SQLToken getNext() {
        return m_next;
    }

    public String getImage() {
        return m_image;
    }

    public Type getType() {
        return m_type;
    }

    public boolean isBind() {
        return m_type == BIND;
    }

    public boolean isPath() {
        return m_type == PATH;
    }

    public boolean isRaw() {
        return m_type == RAW;
    }

    public boolean isSpace() {
        return m_type == SPACE;
    }

}
