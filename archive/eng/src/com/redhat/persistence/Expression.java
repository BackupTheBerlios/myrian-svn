/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.redhat.persistence;

import com.redhat.persistence.common.Path;

/**
 * Expression
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public abstract class Expression {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/Expression.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    public static abstract class Switch {

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
