package com.redhat.persistence.oql;

/**
 * Not
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #8 $ $Date: 2004/02/28 $
 **/

public class Not extends UnaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Not.java#8 $ by $Author: rhs $, $DateTime: 2004/02/28 08:30:26 $";

    public Not(Expression expr) {
        super(expr);
    }

    void frame(Generator gen) {
        super.frame(gen);
        gen.addNulls(this, gen.getNonNull(m_operand));
        gen.addNonNulls(this, gen.getNull(m_operand));
    }

    String emit(Generator gen) {
        String sql = m_operand.emit(gen);
        if (Code.TRUE.equals(sql)) {
            return Code.FALSE;
        } else if (Code.FALSE.equals(sql)) {
            return Code.TRUE;
        } else {
            return "not (" + sql + ")";
        }
    }

    public String toString() {
        return "not (" + m_operand + ")";
    }

    String summary() { return "not"; }

}
