package com.redhat.persistence.oql;

/**
 * Exists
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/01/16 $
 **/

public class Exists extends UnaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Exists.java#2 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

    Exists(Expression query) {
        super(query);
    }

    public String toString() {
        return "exists(" + m_operand + ")";
    }

    String summary() { return "exists"; }

}
