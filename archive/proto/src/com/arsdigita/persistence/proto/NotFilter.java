package com.arsdigita.persistence.proto;

/**
 * NotFilter
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2003/01/13 $
 **/

public class NotFilter extends Filter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/NotFilter.java#3 $ by $Author: rhs $, $DateTime: 2003/01/13 16:40:35 $";

    private Filter m_operand;

    protected NotFilter(Filter operand) {
        m_operand = operand;
    }

    public Filter getOperand() {
        return m_operand;
    }

    public void dispatch(Switch sw) {
        sw.onNot(this);
    }

    public String toString() {
        return "(not " + m_operand + ")";
    }
}
