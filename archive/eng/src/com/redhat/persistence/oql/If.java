/*
 * Copyright (C) 2004 Red Hat, Inc.  All Rights Reserved.
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

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * If
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/08/30 $
 **/

public class If extends Expression {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/oql/If.java#3 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
            (this, merge(cframe.getMap(), aframe.getMap()));
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

    // XXX: this doesn't really make sense with maps
    private ObjectMap merge(ObjectMap a, ObjectMap b) {
        if (a == null || b == null) { return null; }
        ObjectType at = a.getObjectType();
        ObjectType bt = b.getObjectType();
        if (at.isSubtypeOf(bt)) {
            return b;
        }
        if (bt.isSubtypeOf(at)) {
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
