package com.arsdigita.persistence.proto;

/**
 * Filter
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/25 $
 **/

public abstract class Filter {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/Filter.java#1 $ by $Author: rhs $, $DateTime: 2002/11/25 19:30:13 $";

}

abstract class AndFilter extends Filter {

    private Filter m_leftOperand;
    private Filter m_rightOperand;

    protected AndFilter(Filter leftOperand, Filter rightOperand) {
        m_leftOperand = leftOperand;
        m_rightOperand = rightOperand;
    }

}

abstract class OrFilter extends Filter {

    private Filter m_leftOperand;
    private Filter m_rightOperand;

    protected OrFilter(Filter leftOperand, Filter rightOperand) {
        m_leftOperand = leftOperand;
        m_rightOperand = rightOperand;
    }

}

abstract class NotFilter extends Filter {

    private Filter m_operand;

    protected NotFilter(Filter operand) {
        m_operand = operand;
    }

}
