/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.redhat.persistence;

import com.redhat.persistence.common.Path;

/**
 * Expression
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/10/28 $
 **/

public abstract class Expression {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/Expression.java#3 $ by $Author: jorris $, $DateTime: 2003/10/28 18:36:21 $";

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
