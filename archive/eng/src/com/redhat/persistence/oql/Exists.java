/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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

import java.util.*;

/**
 * Exists
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/06/07 $
 **/

public class Exists extends UnaryCondition {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/oql/Exists.java#1 $ by $Author: rhs $, $DateTime: 2004/06/07 13:49:55 $";

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
