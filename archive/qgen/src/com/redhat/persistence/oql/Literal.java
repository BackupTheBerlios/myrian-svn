package com.redhat.persistence.oql;

/**
 * Literal
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/02/06 $
 **/

public class Literal extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Literal.java#3 $ by $Author: rhs $, $DateTime: 2004/02/06 17:43:00 $";

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
        String literal;
        if (m_value instanceof String) {
            literal = quote((String) m_value);
        } else {
            literal = m_value.toString();
        }
        frame.setColumns(new String[] { literal });
        code.addVirtual(this);
        code.setFrame(this, frame);
        return frame;
    }

    // XXX: temporary hack
    private static String quote(String value) {
        StringBuffer result = new StringBuffer(2*value.length());
        result.append("'");
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
            case '\'':
                result.append("'");
                break;
            default:
                result.append(c);
                break;
            }
        }
        result.append("'");
        return result.toString();
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
