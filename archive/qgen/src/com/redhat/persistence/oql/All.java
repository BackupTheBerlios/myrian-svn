package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;

/**
 * All
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/01/27 $
 **/

public class All extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/All.java#1 $ by $Author: rhs $, $DateTime: 2004/01/27 09:26:37 $";

    private String m_type;

    All(String type) {
        m_type = type;
    }

    void graph(Pane pane) {
        throw new UnsupportedOperationException();
    }

    Code.Frame frame(Code code) {
        ObjectType type = code.getType(m_type);
        if (type == null) {
            throw new IllegalStateException("no such type: " + m_type);
        }
        Code.Frame frame = code.frame(code.getType(m_type));
        frame.alias();
        code.setFrame(this, frame);
        return frame;
    }

    void emit(Code code) {
        Code.Frame frame = code.getFrame(this);
        code.append("(select ");
        code.alias(frame.type, frame.getColumns());
        code.append(" from ");
        code.table(frame.type);
        code.append(")");
    }

    public String toString() {
        return "all(" + m_type + ")";
    }

    String summary() {
        return "all: " + m_type;
    }

}
