/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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

/**
 * Range
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/04/07 $
 **/

public abstract class Range extends Expression {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/Range.java#5 $ by $Author: dennis $, $DateTime: 2004/04/07 16:07:11 $";

    Expression m_query;
    Expression m_operand;

    public Range(Expression query, Expression operand) {
        m_query = query;
        m_operand = operand;
    }

    void frame(Generator gen) {
        m_query.frame(gen);
        QFrame query = gen.getFrame(m_query);
        QFrame frame = gen.frame(this, query.getType());
        frame.addChild(query);
        frame.setValues(query.getValues());
        frame.setMappings(query.getMappings());
        m_operand.frame(gen);
        gen.addUses(this, gen.getUses(m_query));
        gen.addUses(this, gen.getUses(m_operand));
    }

    Code emit(Generator gen) {
        return gen.getFrame(this).emit();
    }

    void hash(Generator gen) {
        m_query.hash(gen);
        m_operand.hash(gen);
        gen.hash(getClass());
    }

    abstract String getRangeType();

}
