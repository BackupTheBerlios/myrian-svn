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
 * Limit
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class Limit extends Range {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/oql/Limit.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    public Limit(Expression query, Expression limit) {
        super(query, limit);
    }

    void frame(Generator gen) {
        super.frame(gen);
        QFrame frame = gen.getFrame(this);
        frame.setLimit(m_operand);
        QFrame query = gen.getFrame(m_query);
        frame.setOffset(query.getOffset());
    }

    String getRangeType() {
        return "limit";
    }

    public String toString() {
        return "limit(" + m_query + ", " + m_operand + ")";
    }

    public String summary() {
        return "limit";
    }

}
