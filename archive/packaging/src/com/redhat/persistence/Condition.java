package com.redhat.persistence;

import com.redhat.persistence.common.*;

/**
 * Condition
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 **/

public abstract class Condition extends Expression {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/Condition.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

    public static abstract class Switch {

        public abstract void onAnd(And c);
        public abstract void onOr(Or c);
        public abstract void onNot(Not c);
        public abstract void onEquals(Equals c);
        public abstract void onIn(In c);
        public abstract void onContains(Contains c);

    }

    public abstract void dispatch(Switch sw);

    public void dispatch(Expression.Switch sw) {
        sw.onCondition(this);
    }

    static abstract class Binary extends Condition {
        private Expression m_left;
        private Expression m_right;

        private Binary(Expression left, Expression right) {
            m_left = left;
            m_right = right;
        }

        public Expression getLeft() {
            return m_left;
        }

        public Expression getRight() {
            return m_right;
        }

        // for pretty printing only
        abstract String getOperator();

        public String toString() {
            return "(" + getLeft() + " " + getOperator() + " " + getRight() +
                ")";
        }

    }

    public static class And extends Binary {

        private And(Expression left, Expression right) {
            super(left, right);
        }

        public void dispatch(Switch sw) {
            sw.onAnd(this);
        }

        String getOperator() { return "and"; }

    }

    public static class Or extends Binary {

        private Or(Expression left, Expression right) {
            super(left, right);
        }

        public void dispatch(Switch sw) {
            sw.onOr(this);
        }

        String getOperator() { return "or"; }

    }

    public static class Contains extends Binary {

        private Contains(Expression left, Expression right) {
            super(left, right);
        }

        public void dispatch(Switch sw) {
            sw.onContains(this);
        }

        String getOperator() { return " contains "; }

    }

    public static class Equals extends Binary {

        private Equals(Expression left, Expression right) {
            super(left, right);
        }

        public void dispatch(Switch sw) {
            sw.onEquals(this);
        }

        String getOperator() { return " = "; }

    }

    public static class In extends Binary {

        private In(Expression left, Expression right) {
            super(left, right);
        }

        public void dispatch(Switch sw) {
            sw.onIn(this);
        }

        String getOperator() { return " in "; }

    }

    public static class Not extends Condition {

        private Expression m_expr;

        private Not(Expression expr) {
            m_expr = expr;
        }

        public Expression getExpression() {
            return m_expr;
        }

        public void dispatch(Switch sw) {
            sw.onNot(this);
        }

        public String toString() {
            return "not " + m_expr;
        }

    }

    public static final And and(Expression left, Expression right) {
        return new And(left, right);
    }

    public static final Or or(Expression left, Expression right) {
        return new Or(left, right);
    }

    public static final Equals equals(Expression left, Expression right) {
        return new Equals(left, right);
    }

    public static final Equals equals(Path left, Path right) {
        return equals(Expression.variable(left), Expression.variable(right));
    }

    public static final Contains contains(Expression left, Expression right) {
        return new Contains(left, right);
    }

    public static final Contains contains(Path left, Path right) {
        return contains(Expression.variable(left), Expression.variable(right));
    }

    public static final In in(Expression left, Expression right) {
        return new In(left, right);
    }

    public static final Not not(Expression expr) {
        return new Not(expr);
    }

}
