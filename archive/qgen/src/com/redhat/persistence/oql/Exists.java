package com.redhat.persistence.oql;

import java.util.*;

/**
 * Exists
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #10 $ $Date: 2004/02/28 $
 **/

public class Exists extends UnaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Exists.java#10 $ by $Author: rhs $, $DateTime: 2004/02/28 08:30:26 $";

    public Exists(Expression query) {
        super(query);
    }

    void frame(Generator gen) {
        super.frame(gen);
        QFrame query = gen.getFrame(m_operand);
        gen.addNonNulls(this, query.getValues());
    }

    String emit(Generator gen) {
        QFrame query = gen.getFrame(m_operand);
        if (!query.isSelect()) {
            List values = query.getValues();
            List conds = new ArrayList();
            for (Iterator it = values.iterator(); it.hasNext(); ) {
                QValue value = (QValue) it.next();
                if (value.isNullable()) {
                    conds.add(value + " is not null");
                }
            }
            if (conds.isEmpty()) {
                return Code.TRUE;
            } else {
                return Code.join(conds, " and ");
            }
        } else {
            return "exists (" + m_operand.emit(gen) + ")";
        }
    }

    public String toString() {
        return "exists(" + m_operand + ")";
    }

    String summary() { return "exists"; }

}
