/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.redhat.persistence.oql;

/**
 * BinaryCondition
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/07/15 $
 **/

public abstract class BinaryCondition extends Condition {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/oql/BinaryCondition.java#2 $ by $Author: ashah $, $DateTime: 2004/07/15 12:07:20 $";

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

    private static Code paren(Code sql, Expression e) {
        if (e instanceof BinaryCondition || e instanceof Static) {
            sql = new Code("(").add(sql).add(")");
        }
        return sql;
    }

    static Code emit(Code left, Expression lexpr, String op, Code right,
                     Expression rexpr) {
        return paren(left, lexpr).add(" ").add(op).add(" ")
            .add(paren(right, rexpr));
    }

    Code emit(Code left, String op, Code right) {
        return emit(left, m_left, op, right, m_right);
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
