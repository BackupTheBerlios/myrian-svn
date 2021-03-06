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

/**
 * Not
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/05/02 $
 **/

public class Not extends UnaryCondition {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/oql/Not.java#2 $ by $Author: rhs $, $DateTime: 2004/05/02 13:12:27 $";

    public Not(Expression expr) {
        super(expr);
    }

    void frame(Generator gen) {
        gen.addBoolean(m_operand);
        super.frame(gen);
        gen.addNulls(this, gen.getNonNull(m_operand));
        gen.addNonNulls(this, gen.getNull(m_operand));
    }

    Code emit(Generator gen) {
        Code sql = m_operand.emit(gen);
        if (sql.isTrue()) {
            return Code.FALSE;
        } else if (sql.isFalse()) {
            return Code.TRUE;
        } else {
            return new Code("not (").add(sql).add(")");
        }
    }

    public String toString() {
        return "not (" + m_operand + ")";
    }

    String summary() { return "not"; }

}
