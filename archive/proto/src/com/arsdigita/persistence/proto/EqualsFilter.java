package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.common.*;

/**
 * EqualsFilter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2003/01/15 $
 **/

public class EqualsFilter extends Filter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/EqualsFilter.java#4 $ by $Author: rhs $, $DateTime: 2003/01/15 16:58:00 $";

    private Path m_left;
    private Path m_right;

    protected EqualsFilter(Path left, Path right) {
        m_left = left;
        m_right = right;
    }

    public Path getLeft() {
        return m_left;
    }

    public Path getRight() {
        return m_right;
    }

    public void dispatch(Switch sw) {
        sw.onEquals(this);
    }

    public String toString() {
        return m_left + " = " + m_right;
    }

}
