package com.arsdigita.persistence.proto;

/**
 * OrFilter
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2003/01/13 $
 **/

public class OrFilter extends Filter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/OrFilter.java#4 $ by $Author: rhs $, $DateTime: 2003/01/13 16:40:35 $";

    private Filter m_left;
    private Filter m_right;

    protected OrFilter(Filter left, Filter right) {
        m_left = left;
        m_right = right;
    }

    public Filter getLeft() {
        return m_left;
    }

    public Filter getRight() {
        return m_right;
    }

    public void dispatch(Switch sw) {
        sw.onOr(this);
    }

    public String toString() {
        return "(" + m_left + " or " + m_right + ")";
    }

}
