package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.*;
import com.redhat.persistence.common.*;

import java.util.*;
import java.sql.*;

/**
 * OracleWriter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public class OracleWriter extends ANSIWriter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/engine/rdbms/OracleWriter.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    private static final Expression and(Expression left, Expression right) {
        if (left == null) { return right; }
        if (right == null) { return left; }
        return Condition.and(left, right);
    }

    void writeBind(Object o, int jdbcType) {
        if (o == null) {
            super.writeBind(o, jdbcType);
        } else {
            switch (jdbcType) {
            case Types.BLOB:
                write("empty_blob()");
                break;
            case Types.CLOB:
                write("empty_clob()");
                break;
            default:
                super.writeBind(o, jdbcType);
                break;
            }
        }
    }

    void writeCompound(CompoundJoin join) {
        write(join.getLeft());
        write(",\n     ");
        write(join.getRight());
    }

    public void write(Select select) {
        boolean offlimits = (select.getOffset() != null
                             || select.getLimit() != null);
        if (offlimits) {
            write("select * from (\n");
        }

        writeSelect(select);

        Collection sels = select.getSelections();
        Join join = select.getJoin();
        Expression filter = select.getFilter();

        if (offlimits) {
            if (sels.size() == 0) {
                write(", ");
            } else {
                write(",\n       ");
            }
            write("rownum as rownum__");
        }

        write("\nfrom ");
        write(join);

        filter = and(getJoinConditions(join), filter);

        if (filter != null) {
            write("\nwhere ");
            write(filter);
        }

        writeOrder(select);

        if (offlimits) {
            write(")\nwhere ");
        }

        if (select.getOffset() != null) {
            write("rownum__ > " + select.getOffset());
            if (select.getLimit() != null) {
                write("\nand ");
            }
        }

        if (select.getLimit() != null) {
            Integer upper;

            if (select.getOffset() != null) {
                upper = new Integer(select.getOffset().intValue() +
                                    select.getLimit().intValue());
            } else {
                upper = select.getLimit();
            }

            write("rownum__ <= " + upper);
        }

        if (select.isCount()) {
            write("\n) count__");
        }
    }

    private HashSet m_left = new HashSet();
    private HashSet m_right = new HashSet();

    Expression getJoinConditions(Join j) {
        if (j instanceof CompoundJoin) {
            CompoundJoin cj = (CompoundJoin) j;

            Condition cond = cj.getCondition();

            if (cj instanceof LeftJoin) {
                m_left.add(cond);
            } else if (cj instanceof RightJoin) {
                m_right.add(cond);
            }

            return and(and(getJoinConditions(cj.getLeft()), cj.getCondition()),
                       getJoinConditions(cj.getRight()));
        } else {
            return null;
        }
    }

    private boolean m_isLeft = false;
    private boolean m_isRight = false;

    public void write(Condition.Equals eq) {
        if (m_isLeft || m_isRight) {
            super.write(eq);
        } else {
            m_isLeft = m_left.contains(eq);
            m_isRight = m_right.contains(eq);
            try {
                super.write(eq);
            } finally {
                m_isLeft = false;
                m_isRight = false;
            }
        }
    }

    void writeEquals(Expression left, Expression right) {
        write(left);
        if (m_isLeft) { write("(+)"); }
        write(" = ");
        write(right);
        if (m_isRight) { write("(+)"); }
    }

    public void clear() {
        super.clear();
        m_left.clear();
        m_right.clear();
        m_isLeft = false;
        m_isRight = false;
    }

}
