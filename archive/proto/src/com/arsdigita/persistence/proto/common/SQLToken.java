package com.arsdigita.persistence.proto.common;

/**
 * SQLToken
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/03/15 $
 **/

public class SQLToken {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/common/SQLToken.java#2 $ by $Author: rhs $, $DateTime: 2003/03/15 12:47:21 $";

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

}
