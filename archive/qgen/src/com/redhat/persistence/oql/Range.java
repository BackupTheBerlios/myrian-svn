package com.redhat.persistence.oql;

/**
 * Range
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/01/26 $
 **/

public abstract class Range extends Query {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Range.java#1 $ by $Author: rhs $, $DateTime: 2004/01/26 12:32:44 $";

    Expression m_query;
    Expression m_operand;

    Range(Expression query, Expression operand) {
        m_query = query;
        m_operand = operand;
    }

    void graph(Pane pane) {
        Pane query = pane.frame.graph(m_query);
        Pane op = pane.frame.graph(m_operand);
        pane.type = query.type;
        pane.variables = new UnionVariableNode(query.variables, op.variables);
        pane.injection = query.injection;
        pane.constrained = query.constrained;
        pane.keys = query.keys;
    }

    Code.Frame frame(Code code) {
        return m_query.frame(code);
    }

}
