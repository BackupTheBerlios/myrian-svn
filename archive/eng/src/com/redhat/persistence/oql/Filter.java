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

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Filter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/08/30 $
 **/

public class Filter extends Expression {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/oql/Filter.java#3 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private Expression m_expr;
    private Expression m_condition;

    public Filter(Expression expr, Expression condition) {
        m_expr = expr;
        m_condition = condition;
    }

    void frame(Generator gen) {
        m_expr.frame(gen);
        QFrame expr = gen.getFrame(m_expr);
        QFrame frame = gen.frame(this, expr.getMap());
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
