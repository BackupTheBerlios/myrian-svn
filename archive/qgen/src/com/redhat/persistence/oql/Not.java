package com.redhat.persistence.oql;

/**
 * Not
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/01/16 $
 **/

public class Not extends UnaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Not.java#2 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

    Not(Expression expr) {
        super(expr);
    }

    public String toString() {
        return "not (" + m_operand + ")";
    }

    String summary() { return "not"; }

}
