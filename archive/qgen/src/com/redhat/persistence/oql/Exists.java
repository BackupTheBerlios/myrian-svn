package com.redhat.persistence.oql;

/**
 * Exists
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/01/23 $
 **/

public class Exists extends UnaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Exists.java#3 $ by $Author: rhs $, $DateTime: 2004/01/23 15:34:30 $";

    Exists(Expression query) {
        super(query);
    }

    void emit(Code code) {
        code.append("exists ");
        m_operand.emit(code);
    }

    public String toString() {
        return "exists(" + m_operand + ")";
    }

    String summary() { return "exists"; }

}
