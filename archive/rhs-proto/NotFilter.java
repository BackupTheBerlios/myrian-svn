package com.arsdigita.persistence.proto;

/**
 * NotFilter
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 **/

public abstract class NotFilter extends Filter {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/NotFilter.java#1 $ by $Author: rhs $, $DateTime: 2002/11/27 17:41:53 $";

    private Filter m_operand;

    protected NotFilter(Filter operand) {
        m_operand = operand;
    }

}
