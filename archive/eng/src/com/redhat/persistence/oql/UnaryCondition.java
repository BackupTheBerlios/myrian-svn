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
 * UnaryCondition
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public abstract class UnaryCondition extends Condition {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/oql/UnaryCondition.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
