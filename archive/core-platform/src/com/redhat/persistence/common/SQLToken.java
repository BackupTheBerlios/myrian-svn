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

/**
 * SQLToken
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/03/11 $
 **/

public class SQLToken {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/common/SQLToken.java#4 $ by $Author: vadim $, $DateTime: 2004/03/11 18:13:02 $";

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
