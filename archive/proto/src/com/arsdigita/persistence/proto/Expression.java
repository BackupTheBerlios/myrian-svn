package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.common.*;

/**
 * Expression
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/04/24 $
 **/

public abstract class Expression {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Expression.java#1 $ by $Author: rhs $, $DateTime: 2003/04/24 08:07:11 $";

    public abstract class Switch {

        public abstract void onQuery(Query q);
        public abstract void onCondition(Condition c);
        public abstract void onReference(Reference r);
        public abstract void onPassthrough(Passthrough p);

    }

    public abstract void dispatch(Switch sw);

    public static class Reference extends Expression {
        private Path m_path;

        private Reference(Path path) {
            m_path = path;
        }

        public void dispatch(Switch sw) {
            sw.onReference(this);
        }

        public Path getPath() {
            return m_path;
        }

        public String toString() {
            return "" + m_path;
        }

    }

    public static class Passthrough extends Expression {

        private String m_expr;

        private Passthrough(String expr) {
            m_expr = expr;
        }

        public void dispatch(Switch sw) {
            sw.onPassthrough(this);
        }

        public String getExpression() {
            return m_expr;
        }

    }

}
