package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;

/**
 * All
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/02/06 $
 **/

public class All extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/All.java#2 $ by $Author: rhs $, $DateTime: 2004/02/06 15:43:04 $";

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
        code.setAlias(this, frame.alias(frame.type));
        code.setFrame(this, frame);
        return frame;
    }

    void emit(Code code) {
        Code.Frame frame = code.getFrame(this);
        String alias = code.getAlias(this);
        code.table(frame.type);
        code.append(" ");
        code.append(alias);
    }

    public String toString() {
        return "all(" + m_type + ")";
    }

    String summary() {
        return "all: " + m_type;
    }

}
