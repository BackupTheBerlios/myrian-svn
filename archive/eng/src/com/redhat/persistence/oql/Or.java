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
 * Or
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class Or extends BinaryCondition {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/oql/Or.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    public Or(Expression left, Expression right) {
        super(left, right);
    }

    void frame(Generator gen) {
        gen.addBoolean(m_left);
        gen.addBoolean(m_right);
        super.frame(gen);
    }

    Code emit(Generator gen) {
        Code left = m_left.emit(gen);
        Code right = m_right.emit(gen);
        if (left.isFalse()) {
            return right;
        } else if (right.isFalse()) {
            return left;
        } else if (left.isTrue() || right.isTrue()) {
            return Code.TRUE;
        } else {
            return emit(left, "or", right);
        }
    }

    public String getOperator() {
        return "or";
    }

}
