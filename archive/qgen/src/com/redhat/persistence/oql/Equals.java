package com.redhat.persistence.oql;

import java.util.*;

/**
 * Equals
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #8 $ $Date: 2004/02/21 $
 **/

public class Equals extends BinaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Equals.java#8 $ by $Author: rhs $, $DateTime: 2004/02/21 13:11:19 $";

    public Equals(Expression left, Expression right) {
        super(left, right);
    }

    void frame(Generator gen) {
        super.frame(gen);
        QFrame left = gen.getFrame(m_left);
        QFrame right = gen.getFrame(m_right);
        if (left == null || right == null) { return; }
        List lvals = left.getValues();
        List rvals = right.getValues();
        for (int i = 0; i < lvals.size(); i++) {
            gen.addEquality
                (this, (QValue) lvals.get(i), (QValue) rvals.get(i));
        }
    }

    String emit(Generator gen) {
        String left = m_left.emit(gen);
        String right = m_right.emit(gen);
        if ("null".equals(left)) {
            return right + " is " + left;
        } else if ("null".equals(right)) {
            return left + " is " + right;
        } else {
            return left + " = " + right;
        }
    }

    void graph(Pane pane) {
        super.graph(pane);
        final Pane left = pane.frame.getPane(m_left);
        final Pane right = pane.frame.getPane(m_right);
        pane.constrained = new ConstraintNode() {
            { add(left.variables); add(right.variables); }
            void updateConstraints() {
                if (correlated(left.variables)) {
                    constrain(right, left);
                }
                if (correlated(right.variables)) {
                    constrain(left, right);
                }
            }

            private boolean correlated(VariableNode nd) {
                return nd.lower != null && nd.lower.intValue() > 0;
            }
        };
    }

    Code.Frame frame(Code code) {
        Code.Frame frame = super.frame(code);
        Code.Frame left = code.getFrame(m_left);
        Code.Frame right = code.getFrame(m_right);
        frame.condition(left.getColumns(), right.getColumns());
        return frame;
    }

    void emit(Code code) {
        Code.Frame frame = code.getFrame(this);
        Code.Frame left = code.getFrame(m_left);
        Code.Frame right = code.getFrame(m_right);
        if (left.getColumns().length <= 1) {
            if (left.type == null) {
                code.materialize(m_right);
                code.append(" is ");
                code.materialize(m_left);
            } else if (right.type == null) {
                code.materialize(m_left);
                code.append(" is ");
                code.materialize(m_right);
            } else {
                code.materialize(m_left);
                code.append(" = ");
                code.materialize(m_right);
            }
        } else {
            code.append("exists(select * from ");
            m_left.emit(code);
            code.append(" cross join ");
            m_right.emit(code);
            code.append(" where ");
            code.equals(left.getColumns(), right.getColumns());
            code.append(")");
        }
    }

    String getOperator() {
        return "==";
    }

}
