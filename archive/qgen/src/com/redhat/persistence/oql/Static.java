package com.redhat.persistence.oql;

import com.redhat.persistence.common.*;
import com.redhat.persistence.common.ParseException;

import java.io.*;
import java.util.*;

/**
 * Static
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/02/06 $
 **/

public class Static extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Static.java#2 $ by $Author: rhs $, $DateTime: 2004/02/06 15:43:04 $";

    private String m_sql;
    private Expression[] m_expressions;

    public Static(String sql) {
        m_sql = sql;
        final List exprs = new ArrayList();
        SQLParser p = new SQLParser
            (new StringReader(m_sql),
             new SQLParser.IdentityMapper() {
                 public Path map(Path path) {
                     exprs.add(expression(path));
                     return path;
                 }
             });
        try {
            p.sql();
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        m_expressions =
            (Expression[]) exprs.toArray (new Expression[exprs.size()]);
    }

    private static Expression expression(Path path) {
        if (path.getParent() == null) {
            return new Variable(path.getName());
        } else {
            return new Get(expression(path.getParent()), path.getName());
        }
    }

    void graph(Pane pane) {
        pane.variables = new VariableNode() { void updateVariables() {} };
        Pane[] panes = new Pane[m_expressions.length];
        for (int i = 0; i < panes.length; i++) {
            panes[i] = pane.frame.graph(m_expressions[i]);
            pane.variables =
                new UnionVariableNode(pane.variables, panes[i].variables);
        }
        pane.constrained =
            new ConstraintNode() { void updateConstraints() {} };
    }

    Code.Frame frame(Code code) {
        for (int i = 0; i < m_expressions.length; i++) {
            code.setFrame(m_expressions[i], m_expressions[i].frame(code));
        }
        return null;
    }

    void emit(Code code) {
        SQLParser p = new SQLParser(new StringReader(m_sql));
        try {
            p.sql();
        } catch (ParseException e) {
            throw new IllegalStateException(e.getMessage());
        }
        SQL sql = p.getSQL();
        int index = 0;
        for (SQLToken t = sql.getFirst(); t != null; t = t.getNext()) {
            // XXX: need to handle bind and raw
            if (t.isPath()) {
                code.materialize(m_expressions[index++]);
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
