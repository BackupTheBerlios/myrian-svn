package com.arsdigita.persistence.proto;

/**
 * OrFilter
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/12/04 $
 **/

public abstract class OrFilter extends Filter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/OrFilter.java#2 $ by $Author: rhs $, $DateTime: 2002/12/04 19:18:22 $";

    private Filter m_leftOperand;
    private Filter m_rightOperand;

    protected OrFilter(Filter leftOperand, Filter rightOperand) {
        m_leftOperand = leftOperand;
        m_rightOperand = rightOperand;
    }

    public Filter getLeftOperand() {
        return m_leftOperand;
    }

    public Filter getRightOperand() {
        return m_rightOperand;
    }

    public String toString() {
        return "(" + m_leftOperand + " or " + m_rightOperand + ")";
    }

}
