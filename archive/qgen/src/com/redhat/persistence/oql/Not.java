package com.redhat.persistence.oql;

/**
 * Not
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/02/21 $
 **/

public class Not extends UnaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Not.java#5 $ by $Author: rhs $, $DateTime: 2004/02/21 13:11:19 $";

    public Not(Expression expr) {
        super(expr);
    }

    String emit(Generator gen) {
        return "not (" + m_operand.emit(gen) + ")";
    }

    void opt(Code code) {
        m_operand.opt(code);
    }

    void emit(Code code) {
        code.append("not (");
        m_operand.emit(code);
        code.append(")");
    }

    public String toString() {
        return "not (" + m_operand + ")";
    }

    String summary() { return "not"; }

}
