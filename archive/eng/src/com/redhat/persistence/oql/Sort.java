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

/**
 * Sort
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/08/30 $
 **/

public class Sort extends Expression {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/oql/Sort.java#4 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    public static class Order {
        private Order() {}
    }

    public static final Order ASCENDING = new Order();
    public static final Order DESCENDING = new Order();

    private Expression m_query;
    private Expression m_key;
    private Order m_order;

    public Sort(Expression query, Expression key, Order order) {
        m_query = query;
        m_key = key;
        m_order = order;
    }

    public Sort(Expression query, Expression key) {
        this(query, key, ASCENDING);
    }

    void frame(Generator gen) {
        m_query.frame(gen);
        QFrame query = gen.getFrame(m_query);
        QFrame frame = gen.frame(this, query.getMap());
        frame.addChild(query);
        frame.setValues(query.getValues());
        frame.setMappings(query.getMappings());
        frame.setOrder(m_key, m_order == ASCENDING);
        gen.addUses(this, gen.getUses(m_query));
        gen.push(frame);
        try {
            m_key.frame(gen);
            gen.addUses(this, gen.getUses(m_key));
        } finally {
            gen.pop();
        }
    }

    Code emit(Generator gen) {
        return gen.getFrame(this).emit();
    }

    void hash(Generator gen) {
        m_query.hash(gen);
        m_key.hash(gen);
        gen.hash(m_order == ASCENDING);
        gen.hash(getClass());
    }

    String summary() {
        return "sort";
    }

    public String toString() {
        return summary() + "(" + m_query + ", " + m_key + ")";
    }

}
