package com.redhat.persistence.oql;

import java.util.*;

/**
 * Equals
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/03/25 $
 **/

public class Equals extends BinaryCondition {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/Equals.java#2 $ by $Author: rhs $, $DateTime: 2004/03/25 22:23:19 $";

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
        if (lsql.equals(rsql)) {
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
