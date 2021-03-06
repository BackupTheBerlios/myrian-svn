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
 * Or
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/05/02 $
 **/

public class Or extends BinaryCondition {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/oql/Or.java#2 $ by $Author: rhs $, $DateTime: 2004/05/02 13:12:27 $";

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
