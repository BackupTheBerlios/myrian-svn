package com.arsdigita.persistence.proto;

/**
 * AndFilter
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/12/02 $
 **/

public abstract class AndFilter extends Filter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/AndFilter.java#1 $ by $Author: rhs $, $DateTime: 2002/12/02 12:04:21 $";

    private Filter m_leftOperand;
    private Filter m_rightOperand;

    protected AndFilter(Filter leftOperand, Filter rightOperand) {
        m_leftOperand = leftOperand;
        m_rightOperand = rightOperand;
    }

}
