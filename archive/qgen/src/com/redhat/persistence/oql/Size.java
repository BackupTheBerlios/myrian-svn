package com.redhat.persistence.oql;

/**
 * Size
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/01/26 $
 **/

public class Size extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Size.java#1 $ by $Author: rhs $, $DateTime: 2004/01/26 14:16:33 $";

    private Expression m_query;

    public Size(Expression query) {
        m_query = query;
    }

    void graph(Pane pane) {
        Pane query = pane.frame.graph(m_query);
        pane.variables = query.variables;
    }

    Code.Frame frame(Code code) {
        m_query.frame(code);
        return null;
    }

    void emit(Code code) {
        code.append("(select count(*) from ");
        m_query.emit(code);
        code.append(" sz )");
    }

    public String toString() {
        return "size(" + m_query + ")";
    }

    String summary() {
        return "size";
    }

}
