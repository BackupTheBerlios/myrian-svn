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
 * Size
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/08/30 $
 **/

public class Size extends Expression {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/oql/Size.java#3 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private Expression m_query;

    public Size(Expression query) {
        m_query = query;
    }

    void frame(Generator gen) {
        m_query.frame(gen);
        QFrame query = gen.getFrame(m_query);
        QFrame frame = gen.frame(this, null);
        frame.addChild(query);
        frame.setLimit(query.getLimit());
        frame.setOffset(query.getOffset());
        gen.addUses(this, gen.getUses(m_query));
    }

    Code emit(Generator gen) {
        return gen.getFrame(this).emit();
    }

    void hash(Generator gen) {
        m_query.hash(gen);
        gen.hash(getClass());
    }

    public String toString() {
        return "size(" + m_query + ")";
    }

    String summary() {
        return "size";
    }

}
