package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * If
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/05/05 $
 **/

public class If extends Expression {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/oql/If.java#2 $ by $Author: rhs $, $DateTime: 2004/05/05 22:05:00 $";

    private Expression m_condition;
    private Expression m_consequence;
    private Expression m_alternative;

    public If(Expression condition, Expression consequence,
              Expression alternative) {
        m_condition = condition;
        m_consequence = consequence;
        m_alternative = alternative;
    }

    void hash(Generator gen) {
        m_condition.hash(gen);
        m_consequence.hash(gen);
        m_alternative.hash(gen);
        gen.hash(getClass());
    }

    void frame(Generator gen) {
        m_condition.frame(gen);
        m_consequence.frame(gen);
        m_alternative.frame(gen);
        QFrame cframe = gen.getFrame(m_consequence);
        QFrame aframe = gen.getFrame(m_alternative);
        QFrame frame = gen.frame
            (this, merge(cframe.getType(), aframe.getType()));
        gen.addUses(this, gen.getUses(m_condition));
        gen.addUses(this, gen.getUses(m_consequence));
        gen.addUses(this, gen.getUses(m_alternative));
        frame.setValues
            (Collections.singletonList(frame.getValue(new Emitter() {
                Code emit(Generator gen) {
                    return new Code("case when ")
                        .add(m_condition.emit(gen))
                        .add(" then ")
                        .add(m_consequence.emit(gen))
                        .add(" else ")
                        .add(m_alternative.emit(gen))
                        .add(" end");
                }
            })));
    }

    private ObjectType merge(ObjectType a, ObjectType b) {
        if (a == null) { return b; }
        if (b == null) { return a; }
        if (a.isSubtypeOf(b)) {
            return b;
        }
        if (b.isSubtypeOf(a)) {
            return a;
        }
        throw new IllegalArgumentException
            ("can't merge types: " + a + ", " + b);
    }

    Code emit(Generator gen) {
        return gen.getFrame(this).emit();
    }

    String summary() {
        return "if";
    }

}
