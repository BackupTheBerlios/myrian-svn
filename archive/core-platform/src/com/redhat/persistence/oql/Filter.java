package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Filter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/03/28 $
 **/

public class Filter extends Expression {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/Filter.java#3 $ by $Author: rhs $, $DateTime: 2004/03/28 22:52:45 $";

    private Expression m_expr;
    private Expression m_condition;

    public Filter(Expression expr, Expression condition) {
        m_expr = expr;
        m_condition = condition;
    }

    void frame(Generator gen) {
        m_expr.frame(gen);
        QFrame expr = gen.getFrame(m_expr);
        QFrame frame = gen.frame(this, expr.getType());
        frame.addChild(expr);
        frame.setValues(expr.getValues());
        frame.setMappings(expr.getMappings());
        gen.addUses(this, gen.getUses(m_expr));
        gen.addBoolean(m_condition);
        gen.push(frame);
        try {
            m_condition.frame(gen);
            frame.setCondition(m_condition);
            gen.addUses(this, gen.getUses(m_condition));
        } finally {
            gen.pop();
        }
    }

    Code emit(Generator gen) {
        return gen.getFrame(this).emit();
    }

    void hash(Generator gen) {
        m_expr.hash(gen);
        m_condition.hash(gen);
        gen.hash(getClass());
    }

    public String toString() {
        return "filter(" + m_expr + ", " + m_condition + ")";
    }

    String summary() { return "filter"; }

}
