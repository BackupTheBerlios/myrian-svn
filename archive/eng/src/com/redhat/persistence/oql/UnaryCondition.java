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
 * UnaryCondition
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/06/07 $
 **/

public abstract class UnaryCondition extends Condition {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/oql/UnaryCondition.java#1 $ by $Author: rhs $, $DateTime: 2004/06/07 13:49:55 $";

    Expression m_operand;

    UnaryCondition(Expression operand) {
        m_operand = operand;
    }

    void frame(Generator gen) {
        m_operand.frame(gen);
        gen.addUses(this, gen.getUses(m_operand));
    }

    void hash(Generator gen) {
        m_operand.hash(gen);
        gen.hash(getClass());
    }

}
