package com.arsdigita.persistence.proto;

/**
 * AndFilter
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2003/04/10 $
 **/

public class AndFilter extends Filter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/AndFilter.java#5 $ by $Author: ashah $, $DateTime: 2003/04/10 17:19:22 $";

    private Filter m_left;
    private Filter m_right;

    public AndFilter(Filter left, Filter right) {
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
        sw.onAnd(this);
    }

    public String toString() {
        return "(" + m_left + " and " + m_right + ")";
    }

}
