package com.redhat.persistence.oql;

/**
 * Condition
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/30 $
 **/

public abstract class Condition extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Condition.java#1 $ by $Author: rhs $, $DateTime: 2003/12/30 22:37:27 $";

    void type(Environment env, Frame f) {
        // do nothing
    }

}
