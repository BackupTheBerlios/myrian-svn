package com.arsdigita.persistence.proto.common;

/**
 * SQLToken
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/03/14 $
 **/

public class SQLToken {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/common/SQLToken.java#1 $ by $Author: rhs $, $DateTime: 2003/03/14 13:52:50 $";

    SQLToken m_previous = null;
    SQLToken m_next = null;
    private String m_image;
    private boolean m_bind;

    public SQLToken(String image, boolean bind) {
        m_image = image;
        m_bind = bind;
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

    public boolean isBind() {
        return m_bind;
    }

}
