package com.redhat.persistence.oql;

/**
 * Literal
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/02/09 $
 **/

public class Literal extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Literal.java#6 $ by $Author: ashah $, $DateTime: 2004/02/09 16:16:05 $";

    private Object m_value;

    public Literal(Object value) {
        m_value = value;
    }

    void graph(Pane pane) {
        throw new UnsupportedOperationException();
    }

    Code.Frame frame(Code code) {
        Code.Frame frame;
        if (m_value == null) {
            frame = code.frame(null);
        } else {
            frame = code.frame(code.getType(m_value));
        }
        // XXX: should have option to use bind variables here
        String literal;
        if (m_value instanceof String) {
            literal = quote((String) m_value);
        } else {
            literal = "" + m_value;
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
        if (m_value instanceof String) {
            return "\"" + m_value + "\"";
        } else {
            return "" + m_value;
        }
    }

    String summary() {
        return "" + this;
    }

}
