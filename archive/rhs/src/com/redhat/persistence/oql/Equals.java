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

import java.util.*;

/**
 * Equals
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/05/02 $
 **/

public class Equals extends BinaryCondition {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/oql/Equals.java#2 $ by $Author: rhs $, $DateTime: 2004/05/02 13:12:27 $";

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

        if (lvals.size() != rvals.size()) {
            throw new IllegalStateException
                ("cardinality mismatch\nleft: " + lvals +
                 "\nrvals: " + rvals +
                 "\nleft: " + lexpr +
                 "\nright: " + rexpr);
        }

        List lnull = gen.getNull(lexpr);
        List rnull = gen.getNull(rexpr);
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
                if (lvals.size() != rvals.size()) {
                    throw new IllegalStateException
                        ("signature missmatch: " + lvals + ", " + rvals);
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
                        conds.add(emit(lsql, rsql));
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
            return emit(lsql, rsql);
        }
    }

    private static Code emit(Code left, Code right) {
        if (left.isNull()) {
            return right.add(" is ").add(left);
        } else if (right.isNull()) {
            return left.add(" is ").add(right);
        } else {
            return left.add(" = ").add(right);
        }
    }

    String getOperator() {
        return "==";
    }

}
