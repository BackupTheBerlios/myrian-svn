package com.arsdigita.persistence.proto;

/**
 * NotFilter
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/12/02 $
 **/

public abstract class NotFilter extends Filter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/NotFilter.java#1 $ by $Author: rhs $, $DateTime: 2002/12/02 12:04:21 $";

    private Filter m_operand;

    protected NotFilter(Filter operand) {
        m_operand = operand;
    }

}
