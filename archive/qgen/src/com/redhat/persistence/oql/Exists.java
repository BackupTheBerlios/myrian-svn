package com.redhat.persistence.oql;

/**
 * Exists
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/02/06 $
 **/

public class Exists extends UnaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Exists.java#4 $ by $Author: rhs $, $DateTime: 2004/02/06 15:43:04 $";

    Exists(Expression query) {
        super(query);
    }

    void emit(Code code) {
        code.append("exists ");
        code.materialize(m_operand);
    }

    public String toString() {
        return "exists(" + m_operand + ")";
    }

    String summary() { return "exists"; }

}
