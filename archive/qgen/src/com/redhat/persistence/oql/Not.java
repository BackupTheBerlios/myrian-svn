package com.redhat.persistence.oql;

/**
 * Not
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/02/09 $
 **/

public class Not extends UnaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Not.java#4 $ by $Author: ashah $, $DateTime: 2004/02/09 16:16:05 $";

    public Not(Expression expr) {
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
