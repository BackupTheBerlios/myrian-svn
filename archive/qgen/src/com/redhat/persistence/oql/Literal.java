package com.redhat.persistence.oql;

import java.util.Collection;
import java.util.Iterator;

/**
 * Literal
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #10 $ $Date: 2004/02/21 $
 **/

public class Literal extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Literal.java#10 $ by $Author: rhs $, $DateTime: 2004/02/21 18:22:56 $";

    private Object m_value;

    public Literal(Object value) {
        m_value = value;
    }

    void frame(Generator gen) {
        // XXX: ???
        QFrame frame = gen.frame(this, null);
        frame.setValues(new String[] { convert(m_value) });
    }

    String emit(Generator gen) {
        // XXX: ???
        return gen.getFrame(this).emit();
    }

    private static String convert(Object value) {
        if (value instanceof Collection) {
            Collection c = (Collection) value;
            StringBuffer sb = new StringBuffer("(");
            for (Iterator it = c.iterator(); it.hasNext(); ) {
                sb.append(convert(it.next()));
                if (it.hasNext()) {
                    sb.append(",");
                } else {
                    sb.append(")");
                }
            }

            return sb.toString();
        }

        if (value instanceof com.redhat.persistence.PropertyMap) {
            java.util.Map.Entry me = (java.util.Map.Entry)
                ((com.redhat.persistence.PropertyMap) value).
                entrySet().iterator().next();
            value = me.getValue();
        } else if (value instanceof com.arsdigita.persistence.DataObject) {
            if (((com.arsdigita.persistence.DataObject) value).getOID()
                .getNumberOfProperties() == 1) {
                com.arsdigita.persistence.DataObject dobj =
                    (com.arsdigita.persistence.DataObject) value;
                value = dobj.getOID().get(((com.arsdigita.persistence.metadata.Property) dobj.getObjectType().getKeyProperties().next()).getName());
            } else {
                value = value.toString();
            }
        }

        String literal;
        if (value instanceof String) {
            literal = quote((String) value);
        } else if (value ==  null) {
            literal = "null";
        } else {
            literal = value.toString();
        }

        return literal;
    }

    // XXX: temporary hack
    private static String quote(String value) {
        StringBuffer result = new StringBuffer(2*value.length());
        result.append("'");
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
            case '\'':
                result.append("'");
                break;
            default:
                result.append(c);
                break;
            }
        }
        result.append("'");
        return result.toString();
    }

    public String toString() {
        if (m_value instanceof String) {
            return "\"" + m_value + "\"";
        } else {
            return "" + m_value;
        }
    }

    String summary() {
        return "" + this;
    }

}
