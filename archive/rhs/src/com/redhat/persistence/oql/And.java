/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
 * And
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/04/05 $
 **/

public class And extends BinaryCondition {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/oql/And.java#1 $ by $Author: rhs $, $DateTime: 2004/04/05 15:33:44 $";

    public And(Expression left, Expression right) {
        super(left, right);
    }

    String getOperator() {
        return "and";
    }

    void frame(Generator gen) {
        gen.addBoolean(m_left);
        gen.addBoolean(m_right);
        super.frame(gen);
        gen.addEqualities(this, gen.getEqualities(m_left));
        gen.addEqualities(this, gen.getEqualities(m_right));
        gen.addNulls(this, gen.getNull(m_left));
        gen.addNulls(this, gen.getNull(m_right));
        gen.addNonNulls(this, gen.getNonNull(m_left));
        gen.addNonNulls(this, gen.getNonNull(m_right));
        if (gen.isSufficient(m_left) && gen.isSufficient(m_right)) {
            gen.addSufficient(this);
        }
    }

    Code emit(Generator gen) {
        Code left = m_left.emit(gen);
        Code right = m_right.emit(gen);
        if (left.isTrue()) {
            return right;
        } else if (right.isTrue()) {
            return left;
        } else {
            return emit(left, "and", right);
        }
    }

}
