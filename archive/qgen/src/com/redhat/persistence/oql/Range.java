package com.redhat.persistence.oql;

/**
 * Range
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/02/09 $
 **/

public abstract class Range extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Range.java#4 $ by $Author: ashah $, $DateTime: 2004/02/09 16:16:05 $";

    Expression m_query;
    Expression m_operand;

    public Range(Expression query, Expression operand) {
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
        Code.Frame query = m_query.frame(code);
        Code.Frame frame = code.frame(query.type);
        code.setAlias(this, frame.alias(query.getColumns().length));
        code.setFrame(m_query, query);
        code.setFrame(m_operand, m_operand.frame(code));
        return frame;
    }

    void emit(Code code) {
        Code.Frame query = code.getFrame(m_query);
        code.append("(select ");
        code.alias(query.getColumns());
        code.append(" from ");
        m_query.emit(code);
        code.append(" ");
        code.append(getRangeType());
        code.append(" ");
        code.materialize(m_operand);
        code.append(") " + code.getAlias(this));
    }

    abstract String getRangeType();

}
