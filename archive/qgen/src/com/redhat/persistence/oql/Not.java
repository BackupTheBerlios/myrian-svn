package com.redhat.persistence.oql;

/**
 * Not
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #10 $ $Date: 2004/03/20 $
 **/

public class Not extends UnaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Not.java#10 $ by $Author: rhs $, $DateTime: 2004/03/20 20:50:09 $";

    public Not(Expression expr) {
        super(expr);
    }

    void frame(Generator gen) {
        gen.addBoolean(m_operand);
        super.frame(gen);
        gen.addNulls(this, gen.getNonNull(m_operand));
        gen.addNonNulls(this, gen.getNull(m_operand));
    }

    Code emit(Generator gen) {
        Code sql = m_operand.emit(gen);
        if (sql.isTrue()) {
            return Code.FALSE;
        } else if (sql.isFalse()) {
            return Code.TRUE;
        } else {
            return new Code("not (").add(sql).add(")");
        }
    }

    public String toString() {
        return "not (" + m_operand + ")";
    }

    String summary() { return "not"; }

}
