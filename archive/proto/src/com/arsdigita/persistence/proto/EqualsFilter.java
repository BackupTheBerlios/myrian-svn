package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.common.*;

/**
 * EqualsFilter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2003/02/26 $
 **/

public class EqualsFilter extends Filter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/EqualsFilter.java#5 $ by $Author: rhs $, $DateTime: 2003/02/26 12:01:31 $";

    private Path m_left;
    private Path m_right;

    public EqualsFilter(Path left, Path right) {
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
