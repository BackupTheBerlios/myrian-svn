package com.redhat.persistence.oql;

import java.util.*;

/**
 * Exists
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #11 $ $Date: 2004/03/09 $
 **/

public class Exists extends UnaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Exists.java#11 $ by $Author: rhs $, $DateTime: 2004/03/09 21:48:49 $";

    public Exists(Expression query) {
        super(query);
    }

    void frame(Generator gen) {
        super.frame(gen);
        QFrame query = gen.getFrame(m_operand);
        gen.addNonNulls(this, query.getValues());
    }

    Code emit(Generator gen) {
        QFrame query = gen.getFrame(m_operand);
        if (!query.isSelect()) {
            List values = query.getValues();
            List conds = new ArrayList();
            for (Iterator it = values.iterator(); it.hasNext(); ) {
                QValue value = (QValue) it.next();
                if (value.isNullable()) {
                    conds.add(value.emit().add(" is not null"));
                }
            }
            if (conds.isEmpty()) {
                return Code.TRUE;
            } else {
                return Code.join(conds, " and ");
            }
        } else {
            return new Code("exists (").add(m_operand.emit(gen)).add(")");
        }
    }

    public String toString() {
        return "exists(" + m_operand + ")";
    }

    String summary() { return "exists"; }

}
