/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Filter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/04/05 $
 **/

public class Filter extends Expression {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/oql/Filter.java#1 $ by $Author: rhs $, $DateTime: 2004/04/05 15:33:44 $";

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
