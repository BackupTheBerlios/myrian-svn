package com.arsdigita.persistence.proto;

/**
 * OrFilter
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 **/

public abstract class OrFilter extends Filter {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/OrFilter.java#1 $ by $Author: rhs $, $DateTime: 2002/11/27 17:41:53 $";

    private Filter m_leftOperand;
    private Filter m_rightOperand;

    protected OrFilter(Filter leftOperand, Filter rightOperand) {
        m_leftOperand = leftOperand;
        m_rightOperand = rightOperand;
    }

}
