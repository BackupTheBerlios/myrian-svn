package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.common.*;

import java.util.*;

/**
 * OracleWriter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/07 $
 **/

public class OracleWriter extends ANSIWriter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/OracleWriter.java#1 $ by $Author: rhs $, $DateTime: 2003/05/07 09:50:14 $";

    private static final Expression and(Expression left, Expression right) {
        if (left == null) { return right; }
        if (right == null) { return left; }
        return Condition.and(left, right);
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

        write("select ");

        Collection sels = select.getSelections();
        Join join = select.getJoin();
        Expression filter = select.getFilter();

        if (sels.size() == 0) {
            write("*");
        } else {
            for (Iterator it = sels.iterator(); it.hasNext(); ) {
                Path path = (Path) it.next();
                write(path);
                write(" as ");
                write(select.getAlias(path));
                if (it.hasNext()) {
                    write(",\n       ");
                }
            }
        }

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

        Collection order = select.getOrder();

        if (order.size() > 0) {
            write("\norder by ");
        }

        for (Iterator it = order.iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            write(e);

            if (!select.isAscending(e)) {
                write(" desc");
            }

            if (it.hasNext()) {
                write(", ");
            }
        }

        if (offlimits) {
            write(")\nwhere ");
        }

        if (select.getOffset() != null) {
            Integer lower = new Integer(select.getOffset().intValue() + 1);
            write("rownum__ > " + lower);
            if (select.getLimit() != null) {
                write("\nand ");
            }
        }

        if (select.getLimit() != null) {
            Integer upper;

            if (select.getOffset() != null) {
                upper = new Integer(select.getOffset().intValue() +
                                    select.getLimit().intValue() + 1);
            } else {
                upper = select.getLimit();
            }

            write("rownum__ < " + upper);
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
