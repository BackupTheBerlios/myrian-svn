package com.redhat.persistence.oql;

import java.util.*;

/**
 * Equals
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #12 $ $Date: 2004/02/27 $
 **/

public class Equals extends BinaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Equals.java#12 $ by $Author: rhs $, $DateTime: 2004/02/27 18:00:19 $";

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

    String emit(Generator gen) {
        return emit(gen, m_left, m_right);
    }

    static String emit(Generator gen, Expression lexpr, Expression rexpr) {
        List left = new ArrayList();
        List right = new ArrayList();

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
                for (int i = 0; i < lvals.size(); i++) {
                    left.add("" + (QValue) lvals.get(i));
                    right.add("" + (QValue) rvals.get(i));
                }
            } else {
                // XXX: we can to do something smarter than this for
                // multi column selects
                left.add(lexpr.emit(gen));
                right.add(rexpr.emit(gen));
            }
        } else {
            left.add(lexpr.emit(gen));
            right.add(rexpr.emit(gen));
        }

        List conds = new ArrayList();
        for (int i = 0; i < left.size(); i++) {
            String l = (String) left.get(i);
            String r = (String) right.get(i);
            if (Code.NULL.equals(l)) {
                conds.add(r + " is " + l);
            } else if (Code.NULL.equals(r)) {
                conds.add(l + " is " + r);
            } else if (l.equals(r)) {
                // do nothing
            } else {
                conds.add(l + " = " + r);
            }
        }

        if (conds.isEmpty()) {
            return Code.TRUE;
        } else {
            return Code.join(conds, " and ");
        }
    }

    String getOperator() {
        return "==";
    }

}
