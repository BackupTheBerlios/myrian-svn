package com.redhat.persistence.oql;

/**
 * BinaryCondition
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #9 $ $Date: 2004/03/20 $
 **/

public abstract class BinaryCondition extends Condition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/BinaryCondition.java#9 $ by $Author: rhs $, $DateTime: 2004/03/20 20:50:09 $";

    Expression m_left;
    Expression m_right;

    BinaryCondition(Expression left, Expression right) {
        m_left = left;
        m_right = right;
    }

    void frame(Generator gen) {
        m_left.frame(gen);
        m_right.frame(gen);
        gen.addUses(this, gen.getUses(m_left));
        gen.addUses(this, gen.getUses(m_right));
    }

    private Code paren(Code sql, Expression e) {
        if (e instanceof BinaryCondition) {
            sql = new Code("(").add(sql).add(")");
        }
        return sql;
    }

    Code emit(Code left, String op, Code right) {
        return paren(left, m_left).add(" ").add(op).add(" ")
            .add(paren(right, m_right));
    }

    Code emit(Generator gen) {
        String op = getOperator();
        return emit(m_left.emit(gen), op, m_right.emit(gen));
    }

    private String str(Expression e) {
        if (e instanceof BinaryCondition) {
            return "(" + e + ")";
        } else {
            return e.toString();
        }
    }

    public String toString() {
        return str(m_left) + " " + getOperator() + " " + str(m_right);
    }

    abstract String getOperator();

    String summary() { return getOperator(); }

}
