package com.arsdigita.persistence.proto.engine.rdbms;

/**
 * CompoundJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/02/05 $
 **/

class CompoundJoin extends Join {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/CompoundJoin.java#1 $ by $Author: rhs $, $DateTime: 2003/02/05 18:34:37 $";

    private static class Type {}

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

    public String toString() {
        return m_left + "\n     " + m_type + " " + m_right + " on " +
            m_condition;
    }

}

class CrossJoin extends CompoundJoin {
    public CrossJoin(Join left, Join right) {
        super(left, CompoundJoin.CROSS, right, null);
    }
}

class InnerJoin extends CompoundJoin {
    public InnerJoin(Join left, Join right, Condition cond) {
        super(left, CompoundJoin.INNER, right, cond);
    }
}

class LeftJoin extends CompoundJoin {
    public LeftJoin(Join left, Join right, Condition cond) {
        super(left, CompoundJoin.LEFT, right, cond);
    }
}

class RightJoin extends CompoundJoin {
    public RightJoin(Join left, Join right, Condition cond) {
        super(left, CompoundJoin.RIGHT, right, cond);
    }
}
