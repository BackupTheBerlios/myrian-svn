package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;
import java.sql.*;

/**
 * SQLWriter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2003/03/14 $
 **/

abstract class SQLWriter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/SQLWriter.java#7 $ by $Author: rhs $, $DateTime: 2003/03/14 13:52:50 $";

    private Operation m_op = null;
    private StringBuffer m_sql = new StringBuffer();
    private ArrayList m_bindings = new ArrayList();
    private ArrayList m_types = new ArrayList();

    public String getSQL() {
        return m_sql.toString();
    }

    public Collection getBindings() {
        return m_bindings;
    }

    public void bind(PreparedStatement ps) {
        for (int i = 0; i < m_bindings.size(); i++) {
            int index = i+1;
            Object obj = m_bindings.get(i);
            int type = ((Integer) m_types.get(i)).intValue();

            try {
                if (obj == null) {
                    ps.setNull(index, type);
                } else {
                    Adapter ad = Adapter.getAdapter(obj.getClass());
                    ad.bind(ps, index, obj, type);
                }
            } catch (SQLException e) {
                throw new Error
                    ("SQL error binding [" + (index) + "] to " + obj + ": " +
                     e.getMessage());
            }
        }
    }

    public void write(String str) {
        m_sql.append(str);
    }

    public void write(Path path) {
        if (m_op == null) {
            throw new IllegalStateException
                ("trying to write path outside of operation");
        }

        if (m_op.isParameter(path)) {
            Object value = m_op.get(path);
            if (value instanceof Collection) {
                Collection c = (Collection) value;
                m_sql.append("(");
                for (Iterator it = c.iterator(); it.hasNext(); ) {
                    m_sql.append("?");
                    m_bindings.add(it.next());
                    m_types.add(new Integer(m_op.getType(path)));
                }
                m_sql.append(")");
            } else {
                m_sql.append("?");
                m_bindings.add(value);
                m_types.add(new Integer(m_op.getType(path)));
            }
        } else {
            m_sql.append(path);
        }
    }

    public void write(Operation op) {
        // XXX: this is a hack, for binding to work properly we need to call
        // the Operation version of write.
        Operation old = m_op;
        try {
            m_op = op;
            op.write(this);
        } finally {
            m_op = old;
        }
    }

    public void write(SQL sql) {
        write(sql.getFirst(), null);
    }

    public void write(SQLToken start, SQLToken end) {
        for (SQLToken t = start; t != end; t = t.getNext()) {
            if (t.isBind()) {
                write(Path.get(t.getImage()));
            } else {
                write(t.getImage());
            }
        }
    }

    public void write(StaticOperation sop) {
        SQLBlock block = sop.getSQLBlock();
        SQL sql = block.getSQL();

        boolean first = true;
        boolean execute = false;
        SQLToken written = sql.getFirst();
        SQLToken firstBegin = null;

        for (Iterator it = block.getAssigns().iterator(); it.hasNext(); ) {
            SQLBlock.Assign assign = (SQLBlock.Assign) it.next();
            boolean keep = true;
            Collection bindings = sql.getBindings
                (assign.getBegin(), assign.getEnd());
            for (Iterator iter = bindings.iterator(); iter.hasNext(); ) {
                Path p = (Path) iter.next();
                if (!keep && sop.contains(p)) {
                    throw new Error("missing bind variables");
                }

                if (!sop.contains(p)) {
                    keep = false;
                }
            }

            if (first) {
                first = false;
                firstBegin = assign.getBegin();
            }

            if (keep) {
                if (execute) {
                    write(",");
                } else {
                    write(sql.getFirst(), firstBegin);
                }
                execute = true;
                write(assign.getBegin(), assign.getEnd());
            }

            written = assign.getEnd();
        }

        if (execute || block.getAssigns().size() == 0) {
            write(written, null);
        }
    }

    public void write(Join join) {
        join.write(this);
    }

    public void write(Condition cond) {
        cond.write(this);
    }

    public void write(StaticCondition cond) {
        write(cond.getSQL());
    }

    public abstract void write(Select select);
    public abstract void write(Insert insert);
    public abstract void write(Update update);
    public abstract void write(Delete delete);

    public abstract void write(StaticJoin join);
    public abstract void write(SimpleJoin join);
    public abstract void write(InnerJoin join);
    public abstract void write(LeftJoin join);
    public abstract void write(RightJoin join);
    public abstract void write(CrossJoin join);

    public abstract void write(AndCondition cond);
    public abstract void write(OrCondition cond);
    public abstract void write(NotCondition cond);
    public abstract void write(InCondition cond);
    public abstract void write(EqualsCondition cond);

}


class ANSIWriter extends SQLWriter {

    public void write(Select select) {
        write("select ");

        Collection sels = select.getSelections();
        Join join = select.getJoin();
        Condition cond = select.getCondition();

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

        write("\nfrom ");
        write(join);

        if (cond != null) {
            write("\nwhere ");
            write(cond);
        }

        Collection order = select.getOrder();

        if (order.size() > 0) {
            write("\norder by ");
        }

        for (Iterator it = order.iterator(); it.hasNext(); ) {
            Path p = (Path) it.next();
            write(p);
            if (!select.isAscending(p)) {
                write(" desc");
            }

            if (it.hasNext()) {
                write(", ");
            }
        }
    }

    public void write(Insert insert) {
        Collection cols = insert.getColumns();

        write("insert into ");
        write(insert.getTable().getName());
        write("\n(");

        for (Iterator it = cols.iterator(); it.hasNext(); ) {
            Column col = (Column) it.next();
            write(col.getName());

            if (it.hasNext()) {
                write(", ");
            }
        }

        write(")\nvalues\n(");

        for (Iterator it = cols.iterator(); it.hasNext(); ) {
            Column col = (Column) it.next();
            write(insert.get(col));

            if (it.hasNext()) {
                write(", ");
            }
        }

        write(")");
    }

    public void write(Update update) {
        write("update ");
        write(update.getTable().getName());
        write("\nset ");

        for (Iterator it = update.getColumns().iterator(); it.hasNext(); ) {
            Column col = (Column) it.next();
            write(col.getName());
            write(" = ");
            write(update.get(col));
            if (it.hasNext()) {
                write(",\n    ");
            }
        }

        Condition cond = update.getCondition();
        if (cond != null) {
            write("\nwhere ");
            write(cond);
        }
    }

    public void write(Delete delete) {
        write("delete from ");
        write(delete.getTable().getName());
        Condition cond = delete.getCondition();
        if (cond != null) {
            write("\nwhere ");
            write(cond);
        }
    }


    public void write(StaticJoin join) {
        write("(");
        write(join.getStaticOperation());
        write(") as ");
        write(join.getAlias());
    }

    public void write(SimpleJoin join) {
        write(join.getTable().getName());
        write(" as ");
        write(join.getAlias());
    }

    private void writeCompound(CompoundJoin join) {
        write(join.getLeft());
        write("\n     ");
        write(join.getType().toString());
        write(" ");
        write(join.getRight());
        Condition cond = join.getCondition();
        if (cond != null) {
            write(" on ");
            write(cond);
        }
    }


    public void write(InnerJoin join) {
        writeCompound(join);
    }

    public void write(LeftJoin join) {
        writeCompound(join);
    }

    public void write(RightJoin join) {
        writeCompound(join);
    }

    public void write(CrossJoin join) {
        writeCompound(join);
    }


    public void write(AndCondition cond) {
        write(cond.getLeft());
        write(" and ");
        write(cond.getRight());
    }

    public void write(OrCondition cond) {
        write(cond.getLeft());
        write(" or ");
        write(cond.getRight());
    }

    public void write(NotCondition cond) {
        write("not ");
        write(cond.getOperand());
    }

    public void write(InCondition cond) {
        write(cond.getColumn());
        write(" in (");
        write(cond.getSelect());
        write(")");
    }

    public void write(EqualsCondition cond) {
        write(cond.getLeft());
        write(" = ");
        write(cond.getRight());
    }

}

class UnboundWriter extends ANSIWriter {

    public void write(Path path) {
        write(path.getPath());
    }

}
