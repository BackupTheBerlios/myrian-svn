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

import java.util.*;

/**
 * Exists
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class Exists extends UnaryCondition {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/oql/Exists.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    public Exists(Expression query) {
        super(query);
    }

    void frame(Generator gen) {
        super.frame(gen);
        QFrame query = gen.getFrame(m_operand);
        gen.addNonNulls(this, query.getValues());
    }

    Code emit(Generator gen) {
        QFrame query = gen.getFrame(m_operand);
        if (!query.isSelect()) {
            List values = query.getValues();
            List conds = new ArrayList();
            for (Iterator it = values.iterator(); it.hasNext(); ) {
                QValue value = (QValue) it.next();
                if (value.isNullable()) {
                    conds.add(value.emit().add(" is not null"));
                }
            }
            if (conds.isEmpty()) {
                return Code.TRUE;
            } else {
                return Code.join(conds, " and ");
            }
        } else {
            return new Code("exists (").add(m_operand.emit(gen)).add(")");
        }
    }

    public String toString() {
        return "exists(" + m_operand + ")";
    }

    String summary() { return "exists"; }

}
