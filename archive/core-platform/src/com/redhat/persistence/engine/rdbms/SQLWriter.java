/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.*;
import com.redhat.persistence.Condition;
import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;

import java.util.*;
import java.sql.*;
import java.io.*;

/**
 * SQLWriter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2003/09/04 $
 **/

public abstract class SQLWriter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/engine/rdbms/SQLWriter.java#6 $ by $Author: ashah $, $DateTime: 2003/09/04 14:00:08 $";

    private Operation m_op = null;
    private StringBuffer m_sql = new StringBuffer();
    private ArrayList m_bindings = new ArrayList();
    private ArrayList m_types = new ArrayList();
    private HashSet m_expanded = new HashSet();

    public void clear() {
        m_op = null;
        m_sql = new StringBuffer();
        m_bindings.clear();
        m_types.clear();
        m_expanded.clear();
    }

    public String getSQL() {
        return m_sql.toString();
    }

    public Collection getBindings() {
        return m_bindings;
    }

    public Collection getTypes() {
        return m_types;
    }

    public Collection getTypeNames() {
        ArrayList result = new ArrayList();

        for (Iterator it = getTypes().iterator(); it.hasNext(); ) {
            Integer type = (Integer) it.next();
            result.add(Column.getTypeName(type.intValue()));
        }

        return result;
    }

    public void bind(PreparedStatement ps, StatementLifecycle cycle) {
        for (int i = 0; i < m_bindings.size(); i++) {
            int index = i+1;
            Object obj = m_bindings.get(i);
            int type = ((Integer) m_types.get(i)).intValue();

            try {
                if (cycle != null) { cycle.beginSet(index, type, obj); }
                if (obj == null) {
                    ps.setNull(index, type);
                } else {
                    Adapter ad = Adapter.getAdapter(obj.getClass());
                    ad.bind(ps, index, obj, type);
                }
                if (cycle != null) { cycle.endSet(); }
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
	    if (!m_op.contains(path)) {
		throw new UnboundParameterException(path);
	    }
            Object value = m_op.get(path);
            if (value instanceof Collection) {
                Collection c = (Collection) value;
                m_sql.append("(");
                for (Iterator it = c.iterator(); it.hasNext(); ) {
                    Object o = it.next();
                    writeBind(o, m_op.getType(path));
                    if (it.hasNext()) {
                        m_sql.append(", ");
                    }
                }
                m_sql.append(")");
            } else {
                writeBind(value, m_op.getType(path));
            }
        } else {
            m_sql.append(path);
        }
    }

    void writeBind(Object value, int jdbcType) {
        m_sql.append("?");
        m_bindings.add(value);
        m_types.add(new Integer(jdbcType));
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

    public void write(SQL sql, boolean map) {
        write(sql.getFirst(), null, map);
    }

    public void write(SQLToken start, SQLToken end) {
        write(start, end, false);
    }

    public void write(SQLToken start, SQLToken end, boolean map) {
        Root r = Root.getRoot();

        for (SQLToken t = start; t != end; t = t.getNext()) {
            if (t.isBind()) {
                write(Path.get(t.getImage()));
                continue;
            }

            if (t.isPath() && r.hasObjectType(t.getImage())) {
                ObjectMap om = r.getObjectMap
                    (r.getObjectType(t.getImage()));
                SQLBlock b = om.getRetrieveAll();
                if (b != null) {
                    write(b.getSQL());
                    continue;
                }
            } else if (t.isPath() && map) {
                Path p = Path.get(t.getImage());
                if (m_op.getMapping(p) != null) {
                    write(Expression.variable(p));
                } else {
                    write(t.getImage());
                }
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

    private final Expression.Switch m_esw = new Expression.Switch() {
        public void onQuery(Query q) { write(q); }
        public void onCondition(Condition c) { write(c); }
        public void onVariable(Expression.Variable v) { write(v); }
        public void onValue(Expression.Value v) { write(v); }
        public void onPassthrough(Expression.Passthrough p) { write(p); }
    };

    public void write(Expression expr) {
        expr.dispatch(m_esw);
    }

    private final Condition.Switch m_csw = new Condition.Switch() {
        public void onAnd(Condition.And a) { write(a); }
        public void onOr(Condition.Or o) { write(o); }
        public void onNot(Condition.Not n) { write(n); }
        public void onEquals(Condition.Equals e) { write(e); }
        public void onIn(Condition.In i) { write(i); }
        public void onContains(Condition.Contains c) { write(c); }
    };

    public void write(Condition cond) {
        cond.dispatch(m_csw);
    }

    public void write(Query q) {
        QGen qg = new QGen(q);
        write((Operation) qg.generate());
    }

    public void write(Expression.Variable v) {
        if (m_expanded.contains(v)) {
            write(v.getPath());
        } else {
            Path[] cols = m_op.getMapping(v.getPath());
            if (cols == null) { throw new Error("no mapping: " + v); }
            if (cols.length != 1) {
                throw new Error("expands to wrong multiplicity");
            }
            write(cols[0]);
        }
    }

    public void write(Expression.Value v) {
        writeBind(v.getValue(), RDBMSEngine.getType(v.getValue()));
    }

    public void write(Expression.Passthrough e) {
        SQLParser p = new SQLParser(new StringReader(e.getExpression()));
        try {
            p.sql();
        } catch (ParseException pe) {
            throw new Error(pe.getMessage());
        }

        write(p.getSQL(), true);
    }

    public void write(Condition.And cond) {
        write(cond.getLeft());
        write(" and ");
        write(cond.getRight());
    }

    public void write(Condition.Or cond) {
        write(cond.getLeft());
        write(" or ");
        write(cond.getRight());
    }

    public void write(Condition.Not cond) {
        write("not ");
        write(cond.getExpression());
    }

    public void write(Condition.In cond) {
        write(cond.getLeft());
        write(" in (");
        write(cond.getRight());
        write(")");
    }


    private boolean isExpandable(Expression expr) {
        return !m_expanded.contains(expr)
            && expr instanceof Expression.Variable
            || expr instanceof Expression.Value;
    }

    private Path[] expand(Expression expr) {
        final Path[][] result = { null };

        expr.dispatch(new Expression.Switch() {
            public void onVariable(Expression.Variable v) {
                if (m_op.isParameter(v.getPath())) {
                    result[0] = new Path[] { v.getPath() };
                } else {
                    result[0] = m_op.getMapping(v.getPath());
                    if (result[0] == null) {
                        throw new IllegalStateException
                            ("no expansion for expr: " + v);
                    }
                }
            }
            public void onValue(Expression.Value v) {
                throw new Error("not implemented");
            }
            public void onQuery(Query q) {
                throw new Error("not implemented");
            }
            public void onPassthrough(Expression.Passthrough p) {
                throw new Error("not implemented");
            }
            public void onCondition(Condition c) {
                throw new Error("not implemented");
            }
        });

        return result[0];
    }

    private Expression expand(Expression left, Expression right) {
        if (!isExpandable(left)) {
            throw new IllegalArgumentException("not expandable: " + left);
        }
        if (!isExpandable(right)) {
            throw new IllegalArgumentException("not expandable: " + right);
        }

        Expression result = null;

        Path[] leftCols = expand(left);
        Path[] rightCols = expand(right);
        if (leftCols.length != rightCols.length) {
            throw new IllegalArgumentException
                (left + ": " + Arrays.asList(leftCols) + ", " +
                 right + ": " + Arrays.asList(rightCols));
        }

        for (int i = 0; i < leftCols.length; i++) {
            Expression l = Expression.variable(leftCols[i]);
            Expression r = Expression.variable(rightCols[i]);
            m_expanded.add(l);
            m_expanded.add(r);
            Expression eq = Condition.equals(l, r);
            if (result == null) {
                result = eq;
            } else {
                result = Condition.and(result, eq);
            }
        }

        return result;
    }

    private void writeLogicalEquals(Expression left, Expression right) {
        if (isExpandable(left) && isExpandable(right)) {
            write(expand(left, right));
        } else {
            writeEquals(left, right);
        }
    }

    void writeEquals(Expression left, Expression right) {
        write(left);
        write(" = ");
        write(right);
    }

    public void write(Condition.Equals cond) {
        writeLogicalEquals(cond.getLeft(), cond.getRight());
    }

    public void write(Condition.Contains cond) {
        writeLogicalEquals(cond.getLeft(), cond.getRight());
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

}


class ANSIWriter extends SQLWriter {

    protected void writeSelect(Select select) {
        write("select ");

        Collection sels = select.getSelections();

        if (select.isCount()) {
            write("count(*)\nfrom (\nselect 1");
        } else {
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
        }
    }

    protected void writeOrder(Select select) {
        if (select.isCount()) { return; }

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
    }

    public void write(Select select) {
        writeSelect(select);

        Join join = select.getJoin();
        Expression filter = select.getFilter();

        write("\nfrom ");
        write(join);

        if (filter != null) {
            write("\nwhere ");
            write(filter);
        }

        writeOrder(select);

        if (select.getOffset() != null) {
            write("\noffset " + select.getOffset());
        }

        if (select.getLimit() != null) {
            write("\nlimit " + select.getLimit());
        }

        if (select.isCount()) {
            write("\n) count__");
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
        // XXX: this is a hack, for binding to work properly we need to call
        // the Operation version of write.
        write((Operation) join.getStaticOperation());
        write(") ");
        write(join.getAlias());
    }

    public void write(SimpleJoin join) {
        write(join.getTable().getName());
        write(" ");
        write(join.getAlias());
    }


    void writeCompound(CompoundJoin join) {
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

}

class RetainUpdatesWriter extends ANSIWriter {

    public void write(StaticOperation sop) {
	write(sop.getSQLBlock().getSQL());
    }

}

class UnboundWriter extends ANSIWriter {

    public void write(Path path) {
        write(path.getPath());
    }

}
