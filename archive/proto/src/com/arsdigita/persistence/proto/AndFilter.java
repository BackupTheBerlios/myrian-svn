package com.arsdigita.persistence.proto;

/**
 * AndFilter
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2003/01/06 $
 **/

public abstract class AndFilter extends Filter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/AndFilter.java#3 $ by $Author: rhs $, $DateTime: 2003/01/06 16:31:02 $";

    private Filter m_left;
    private Filter m_right;

    protected AndFilter(Filter left, Filter right) {
        m_left = left;
        m_right = right;
    }

    public Filter getLeft() {
        return m_left;
    }

    public Filter getRight() {
        return m_right;
    }

    public String toString() {
        return "(" + m_left + " and " + m_right + ")";
    }

}
