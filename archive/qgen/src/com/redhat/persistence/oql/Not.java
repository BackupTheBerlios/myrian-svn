package com.redhat.persistence.oql;

/**
 * Not
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/02/21 $
 **/

public class Not extends UnaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Not.java#6 $ by $Author: rhs $, $DateTime: 2004/02/21 18:22:56 $";

    public Not(Expression expr) {
        super(expr);
    }

    String emit(Generator gen) {
        return "not (" + m_operand.emit(gen) + ")";
    }

    public String toString() {
        return "not (" + m_operand + ")";
    }

    String summary() { return "not"; }

}
