package com.redhat.persistence.oql;

/**
 * Literal
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/01/27 $
 **/

public class Literal extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Literal.java#1 $ by $Author: rhs $, $DateTime: 2004/01/27 09:26:37 $";

    private Object m_value;

    Literal(Object value) {
        m_value = value;
    }

    void graph(Pane pane) {
        throw new UnsupportedOperationException();
    }

    Code.Frame frame(Code code) {
        return null;
    }

    void emit(Code code) {
        // XXX: should have option to use bind variables here
        code.append("" + m_value);
    }

    public String toString() {
        return "" + m_value;
    }

    String summary() {
        return "" + this;
    }

}
