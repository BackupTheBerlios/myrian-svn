package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;

/**
 * CompoundJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2003/04/30 $
 **/

abstract class CompoundJoin extends Join {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/CompoundJoin.java#4 $ by $Author: rhs $, $DateTime: 2003/04/30 10:11:14 $";

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
