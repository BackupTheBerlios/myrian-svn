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
package com.redhat.persistence.oql;

/**
 * BinaryCondition
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/08/30 $
 **/

public abstract class BinaryCondition extends Condition {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/oql/BinaryCondition.java#3 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
