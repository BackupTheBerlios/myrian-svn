package com.redhat.persistence.oql;

/**
 * Range
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/02/21 $
 **/

public abstract class Range extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Range.java#5 $ by $Author: rhs $, $DateTime: 2004/02/21 13:11:19 $";

    Expression m_query;
    Expression m_operand;

    public Range(Expression query, Expression operand) {
        m_query = query;
        m_operand = operand;
    }

    void frame(Generator gen) {
        m_query.frame(gen);
        QFrame query = gen.getFrame(m_query);
        QFrame frame = gen.frame(this, query.getType());
        frame.addChild(query);
        frame.setValues(query.getValues());
        m_operand.frame(gen);
    }

    String emit(Generator gen) {
        return gen.getFrame(this).emit();
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
        m_operand.frame(code);
        code.setFrame(this, frame);
        return frame;
    }

    void opt(Code code) {
        m_query.opt(code);
        Code.Frame frame = code.getFrame(this);
        Code.Frame query = code.getFrame(m_query);
        //frame.suckAll(query);
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
