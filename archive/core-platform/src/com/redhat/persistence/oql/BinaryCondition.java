package com.redhat.persistence.oql;

/**
 * BinaryCondition
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/03/28 $
 **/

public abstract class BinaryCondition extends Condition {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/BinaryCondition.java#3 $ by $Author: rhs $, $DateTime: 2004/03/28 22:52:45 $";

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
        if (e instanceof BinaryCondition || e instanceof Static) {
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

    void hash(Generator gen) {
        m_left.hash(gen);
        m_right.hash(gen);
        gen.hash(getClass());
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
