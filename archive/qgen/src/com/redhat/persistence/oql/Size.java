package com.redhat.persistence.oql;

/**
 * Size
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/02/21 $
 **/

public class Size extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Size.java#3 $ by $Author: rhs $, $DateTime: 2004/02/21 13:11:19 $";

    private Expression m_query;

    public Size(Expression query) {
        m_query = query;
    }

    void frame(Generator gen) {
        m_query.frame(gen);
        // make sure we're being called on something with a QFrame
        gen.getFrame(m_query);
    }

    String emit(Generator gen) {
        QFrame query = gen.getFrame(m_query);
        return "(select count(*) from " + query.emit(false) + ")";
    }

    void graph(Pane pane) {
        Pane query = pane.frame.graph(m_query);
        pane.variables = query.variables;
    }

    Code.Frame frame(Code code) {
        m_query.frame(code);
        return null;
    }

    void opt(Code code) {
        m_query.opt(code);
    }

    void emit(Code code) {
        code.append("(select count(*) from ");
        m_query.emit(code);
        code.append(") " + code.var("c"));
    }

    public String toString() {
        return "size(" + m_query + ")";
    }

    String summary() {
        return "size";
    }

}
