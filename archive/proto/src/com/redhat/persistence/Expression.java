package com.redhat.persistence;

import com.redhat.persistence.common.*;

/**
 * Expression
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public abstract class Expression {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/Expression.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    public static abstract class Switch {

        public abstract void onQuery(Query q);
        public abstract void onCondition(Condition c);
        public abstract void onVariable(Variable r);
        public abstract void onValue(Value v);
        public abstract void onPassthrough(Passthrough p);

    }

    public abstract void dispatch(Switch sw);

    public static class Variable extends Expression {
        private Path m_path;

        private Variable(Path path) {
            m_path = path;
        }

        public void dispatch(Switch sw) {
            sw.onVariable(this);
        }

        public Path getPath() {
            return m_path;
        }

        public String toString() {
            return "" + m_path;
        }

    }

    public static class Value extends Expression {

        private Object m_value;

        private Value(Object value) {
            m_value = value;
        }

        public void dispatch(Switch sw) {
            sw.onValue(this);
        }

        public Object getValue() {
            return m_value;
        }

        public String toString() {
            return "" + m_value;
        }

    }

    public static class Passthrough extends Expression {

        private String m_expr;

        private Passthrough(String expr) {
            m_expr = expr;
        }

        public void dispatch(Switch sw) {
            sw.onPassthrough(this);
        }

        public String getExpression() {
            return m_expr;
        }

        public String toString() {
            return m_expr;
        }

    }

    public static final Variable variable(Path path) {
        return new Variable(path);
    }

    public static final Value value(Object value) {
        return new Value(value);
    }

    public static final Passthrough passthrough(String expr) {
        return new Passthrough(expr);
    }

}
