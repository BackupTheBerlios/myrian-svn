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

import java.util.*;

/**
 * Equals
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/08/30 $
 **/

public class Equals extends BinaryCondition {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/oql/Equals.java#4 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    public Equals(Expression left, Expression right) {
        super(left, right);
    }

    void frame(Generator gen) {
        super.frame(gen);

        if (!gen.hasFrame(m_left) || !gen.hasFrame(m_right)) { return; }

        QFrame left = gen.getFrame(m_left);
        QFrame right = gen.getFrame(m_right);

        equate(gen, this, left, right);
    }

    static void equate(Generator gen, Expression expr, QFrame left,
                       QFrame right) {
        Expression lexpr = left.getExpression();
        Expression rexpr = right.getExpression();

        List lvals = left.getValues();
        List rvals = right.getValues();

        List lnull = gen.getNull(lexpr);
        List rnull = gen.getNull(rexpr);

        if (lvals.size() != rvals.size()) {
            if (lvals.size() == 1 && lnull.containsAll(lvals)) {
                lvals = repeat(lvals.get(0), rvals.size());
            } else if (rvals.size() == 1 && rnull.containsAll(rvals)) {
                rvals = repeat(rvals.get(0), lvals.size());
            } else {
                throw new IllegalStateException
                    ("cardinality mismatch\nlvals: " + lvals +
                     "\nrvals: " + rvals +
                     "\nleft: " + lexpr +
                     "\nright: " + rexpr);
            }
        }

        List lnonnull = gen.getNonNull(lexpr);
        List rnonnull = gen.getNonNull(rexpr);

        for (int i = 0; i < lvals.size(); i++) {
            QValue lval = (QValue) lvals.get(i);
            QValue rval = (QValue) rvals.get(i);
            gen.addEquality(expr, lval, rval);
            if (lnull.contains(lval)) {
                gen.addNull(expr, rval);
            }
            if (lnonnull.contains(lval)) {
                gen.addNonNull(expr, rval);
            }
            if (rnull.contains(rval)) {
                gen.addNull(expr, lval);
            }
            if (rnonnull.contains(rval)) {
                gen.addNonNull(expr, lval);
            }
        }

        gen.addSufficient(expr);
    }

    private static List repeat(Object o, int size) {
        List l = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            l.add(o);
        }
        return l;
    }

    Code emit(Generator gen) {
        return emit(gen, m_left, m_right);
    }

    static Code emit(Generator gen, Expression lexpr, Expression rexpr) {
        if (gen.hasFrame(lexpr) && gen.hasFrame(rexpr)) {
            QFrame lframe = gen.getFrame(lexpr);
            QFrame rframe = gen.getFrame(rexpr);
            if (!lframe.isSelect() && !rframe.isSelect()) {
                List lvals = lframe.getValues();
                List rvals = rframe.getValues();
                List lnull = gen.getNull(lexpr);
                List rnull = gen.getNull(rexpr);
                if (lvals.size() != rvals.size()) {
                    if (lvals.size() == 1 && lnull.containsAll(lvals)) {
                        lvals = repeat(lvals.get(0), rvals.size());
                    } else if (rvals.size() == 1 && rnull.containsAll(rvals)) {
                        rvals = repeat(rvals.get(0), lvals.size());
                    } else {
                        throw new IllegalStateException
                            ("signature missmatch: " + lvals + ", " + rvals);
                    }
                }
                List conds = new ArrayList();
                for (int i = 0; i < lvals.size(); i++) {
                    QValue l = (QValue) lvals.get(i);
                    QValue r = (QValue) rvals.get(i);
                    Code lsql = l.emit();
                    Code rsql = r.emit();
                    if (lsql.isNull()) {
                        if (!r.isNullable()) {
                            return Code.FALSE;
                        }
                    } else if (rsql.isNull()) {
                        if (!l.isNullable()) {
                            return Code.FALSE;
                        }
                    }

                    if (!lsql.equals(rsql)) {
                        conds.add(emit(lsql, null, rsql, null));
                    }
                }
                if (conds.isEmpty()) {
                    return Code.TRUE;
                } else {
                    return Code.join(conds, " and ");
                }
            }
        }

        // XXX: we can to do something smarter than this for
        // multi column selects
        Code lsql = lexpr.emit(gen);
        Code rsql = rexpr.emit(gen);

        // we need the isEmpty test because we don't want to eliminate
        // redundent bind var comparison the first time a query is
        // cached since the sql may not be redundent for subsequent
        // query executions

        if (lsql.getBindings().isEmpty() && lsql.equals(rsql)) {
            return Code.TRUE;
        } else {
            return emit(lsql, lexpr, rsql, rexpr);
        }
    }

    private static Code emit(Code lc, Expression le, Code rc, Expression re) {
        if (lc.isNull()) {
            return emit(rc, re, "is", lc, le);
        } else if (rc.isNull()) {
            return emit(lc, le, "is", rc, re);
        } else {
            return emit(lc, le, "=", rc, re);
        }
    }

    String getOperator() {
        return "==";
    }

}
