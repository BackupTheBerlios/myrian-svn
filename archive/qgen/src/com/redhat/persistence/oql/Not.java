package com.redhat.persistence.oql;

/**
 * Not
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/01/23 $
 **/

public class Not extends UnaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Not.java#3 $ by $Author: rhs $, $DateTime: 2004/01/23 15:34:30 $";

    Not(Expression expr) {
        super(expr);
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
