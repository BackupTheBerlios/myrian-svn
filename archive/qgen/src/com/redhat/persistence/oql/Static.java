package com.redhat.persistence.oql;

import com.redhat.persistence.common.*;
import com.redhat.persistence.common.ParseException;
import com.redhat.persistence.metadata.ObjectMap;
import com.redhat.persistence.metadata.ObjectType;

import java.io.*;
import java.util.*;

/**
 * Static
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/02/13 $
 **/

public class Static extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Static.java#3 $ by $Author: ashah $, $DateTime: 2004/02/13 21:49:42 $";

    private SQL m_sql;
    private Map m_bindings;
    private boolean m_mapPaths;

    public Static(String sql) {
        this(sql, Collections.EMPTY_MAP, true);
    }

    public Static(SQL sql) {
        this(sql, Collections.EMPTY_MAP, true);
    }

    public Static(String sql, Map bindings, boolean mapPaths) {
        this(parse(sql), bindings, mapPaths);
    }

    public Static(SQL sql, Map bindings, boolean mapPaths) {
        m_sql = sql;
        m_bindings = bindings;
        m_mapPaths = mapPaths;
    }

    private static SQL parse(String sql) {
        final List exprs = new ArrayList();
        SQLParser p = new SQLParser(new StringReader(sql));
        try {
            p.sql();
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        return p.getSQL();
    }

    private static Expression expression(Path path) {
        if (path.getParent() == null) {
            return new Variable(path.getName());
        } else {
            return new Get(expression(path.getParent()), path.getName());
        }
    }

    void graph(Pane pane) {
//         pane.variables = new VariableNode() { void updateVariables() {} };
//         Pane[] panes = new Pane[m_expressions.length];
//         for (int i = 0; i < panes.length; i++) {
//             panes[i] = pane.frame.graph(m_expressions[i]);
//             pane.variables =
//                 new UnionVariableNode(pane.variables, panes[i].variables);
//         }
//         pane.constrained =
//             new ConstraintNode() { void updateConstraints() {} };
        throw new UnsupportedOperationException();
    }

    Code.Frame frame(Code code) {
        List exprs = new ArrayList();

        for(SQLToken t = m_sql.getFirst(); t != null; t = t.getNext()) {
            if (t.isBind()) {
                // XXX :foo.id needs to be handled
                Literal value = new Literal(m_bindings.get(t.getImage()));
                exprs.add(value);
            } else if (t.isPath() && m_mapPaths) {
                String image = t.getImage();
                ObjectType ot = code.getType(image);
                if (ot == null) {
                    exprs.add(expression(Path.get(image)));
                } else {
                    ObjectMap map = ot.getRoot().getObjectMap(ot);
                    exprs.add(new Static
                              (map.getRetrieveAll().getSQL(),
                               m_bindings,
                               false) {
                        void emit(Code code) {
                            code.append("(");
                            super.emit(code);
                            code.append(") ");
                            code.append(code.var("st"));
                        }
                    });
                }
            }
        }

        for (Iterator it = exprs.iterator(); it.hasNext(); ) {
            Expression expr = (Expression) it.next();
            code.setFrame(expr, expr.frame(code));
        }

        code.setChildren(this, exprs);
        code.addVirtual(this);
        return code.frame(null);
    }

    void emit(Code code) {
        int index = 0;
        for (SQLToken t = m_sql.getFirst(); t != null; t = t.getNext()) {
            // XXX: need to handle raw
            if ((t.isPath() && m_mapPaths) || t.isBind()) {
                Expression expr =
                    (Expression) code.getChildren(this).get(index++);
                code.materialize(expr);
            } else {
                code.append(t.getImage());
            }
        }
    }

    public String toString() {
        return "{" + m_sql + "}";
    }

    String summary() {
        return "static: " + this;
    }

}
