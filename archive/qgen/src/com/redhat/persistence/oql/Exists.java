package com.redhat.persistence.oql;

import java.util.*;

/**
 * Exists
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #8 $ $Date: 2004/02/21 $
 **/

public class Exists extends UnaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Exists.java#8 $ by $Author: rhs $, $DateTime: 2004/02/21 18:22:56 $";

    public Exists(Expression query) {
        super(query);
    }

    String emit(Generator gen) {
        QFrame query = gen.getFrame(m_operand);
        if (!query.isSelect()) {
            List values = query.getValues();
            StringBuffer buf = new StringBuffer();
            for (Iterator it = values.iterator(); it.hasNext(); ) {
                QValue value = (QValue) it.next();
                buf.append(value);
                buf.append(" is not null");
                if (it.hasNext()) {
                    buf.append(" and ");
                }
            }
            return buf.toString();
        } else {
            return "exists (" + m_operand.emit(gen) + ")";
        }
    }

    public String toString() {
        return "exists(" + m_operand + ")";
    }

    String summary() { return "exists"; }

}
