package com.arsdigita.persistence.proto;

/**
 * NotFilter
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/12/04 $
 **/

public abstract class NotFilter extends Filter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/NotFilter.java#2 $ by $Author: rhs $, $DateTime: 2002/12/04 19:18:22 $";

    private Filter m_operand;

    protected NotFilter(Filter operand) {
        m_operand = operand;
    }

    public Filter getOperand() {
        return m_operand;
    }

    public String toString() {
        return "(not " + m_operand + ")";
    }
}
