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
package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.Condition;

/**
 * CompoundJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

abstract class CompoundJoin extends Join {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/engine/rdbms/CompoundJoin.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
