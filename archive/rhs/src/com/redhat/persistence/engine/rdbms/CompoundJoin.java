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
package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.Condition;

/**
 * CompoundJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/04/05 $
 **/

abstract class CompoundJoin extends Join {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/engine/rdbms/CompoundJoin.java#2 $ by $Author: rhs $, $DateTime: 2004/04/05 15:33:44 $";

    public static class Type {}

    public static final Type INNER = new Type() {
            public String toString() {
                return "join";
            }
        };
    public static final Type CROSS = new Type() {
            public String toString() {
                return "cross join";
            }
        };
    public static final Type LEFT = new Type() {
            public String toString() {
                return "left join";
            }
        };
    public static final Type RIGHT = new Type() {
            public String toString() {
                return "right join";
            }
        };

    private Join m_left;
    private Type m_type;
    private Join m_right;
    private Condition m_condition;

    public CompoundJoin(Join left, Type type, Join right,
                        Condition condition) {
        m_left = left;
        m_type = type;
        m_right = right;
        m_condition = condition;
    }

    public Join getLeft() {
        return m_left;
    }

    public Type getType() {
        return m_type;
    }

    public Join getRight() {
        return m_right;
    }

    public Condition getCondition() {
        return m_condition;
    }

}

class CrossJoin extends CompoundJoin {

    public CrossJoin(Join left, Join right) {
        super(left, CompoundJoin.CROSS, right, null);
    }

    void write(SQLWriter w) {
        w.write(this);
    }

}

class InnerJoin extends CompoundJoin {

    public InnerJoin(Join left, Join right, Condition cond) {
        super(left, CompoundJoin.INNER, right, cond);
    }

    void write(SQLWriter w) {
        w.write(this);
    }

}

class LeftJoin extends CompoundJoin {

    public LeftJoin(Join left, Join right, Condition cond) {
        super(left, CompoundJoin.LEFT, right, cond);
    }

    void write(SQLWriter w) {
        w.write(this);
    }

}

class RightJoin extends CompoundJoin {

    public RightJoin(Join left, Join right, Condition cond) {
        super(left, CompoundJoin.RIGHT, right, cond);
    }

    void write(SQLWriter w) {
        w.write(this);
    }

}
