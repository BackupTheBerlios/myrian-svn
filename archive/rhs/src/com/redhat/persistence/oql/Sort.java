/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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

/**
 * Sort
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/04/05 $
 **/

public class Sort extends Expression {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/oql/Sort.java#1 $ by $Author: rhs $, $DateTime: 2004/04/05 15:33:44 $";

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
        QFrame frame = gen.frame(this, query.getType());
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
        return "sort(" + m_query + ", " + m_key + ")";
    }

}
