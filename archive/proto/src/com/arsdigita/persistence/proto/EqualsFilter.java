package com.arsdigita.persistence.proto;

/**
 * EqualsFilter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/01/06 $
 **/

public abstract class EqualsFilter extends Filter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/EqualsFilter.java#2 $ by $Author: rhs $, $DateTime: 2003/01/06 16:31:02 $";

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

    public String toString() {
        return m_left + " = " + m_right;
    }

}
