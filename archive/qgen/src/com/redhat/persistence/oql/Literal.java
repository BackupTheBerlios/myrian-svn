package com.redhat.persistence.oql;

/**
 * Literal
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/02/06 $
 **/

public class Literal extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Literal.java#2 $ by $Author: rhs $, $DateTime: 2004/02/06 15:43:04 $";

    private Object m_value;

    Literal(Object value) {
        m_value = value;
    }

    void graph(Pane pane) {
        throw new UnsupportedOperationException();
    }

    Code.Frame frame(Code code) {
        Code.Frame frame = code.frame(code.getType(m_value));
        // XXX: should have option to use bind variables here
        frame.setColumns(new String[] { m_value.toString() });
        code.addVirtual(this);
        code.setFrame(this, frame);
        return frame;
    }

    void emit(Code code) {
        code.materialize(this);
    }

    public String toString() {
        return "" + m_value;
    }

    String summary() {
        return "" + this;
    }

}
