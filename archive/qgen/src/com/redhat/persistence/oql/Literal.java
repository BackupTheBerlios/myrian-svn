package com.redhat.persistence.oql;

import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;

import java.util.*;

/**
 * Literal
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #11 $ $Date: 2004/02/24 $
 **/

public class Literal extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Literal.java#11 $ by $Author: rhs $, $DateTime: 2004/02/24 21:30:52 $";

    private Object m_value;

    public Literal(Object value) {
        m_value = value;
    }

    void frame(Generator gen) {
        // XXX: ???
        QFrame frame = gen.frame(this, null);
        List result = new ArrayList();
        convert(m_value, result, gen.getRoot());
        if (result.size() == 0) {
            throw new IllegalStateException
                ("unable to convert value: " + m_value);
        }
        frame.setValues((String[]) result.toArray(new String[result.size()]));
    }

    String emit(Generator gen) {
        // XXX: ???
        return gen.getFrame(this).emit();
    }

    private static void convert(Object value, List result, Root root) {
        if (value == null) {
            result.add("null");
        } else if (value instanceof Boolean) {
            if (value.equals(Boolean.TRUE)) {
                result.add("'1'");
            } else {
                result.add("'0'");
            }
        } else if (value instanceof Number) {
            result.add(value.toString());
        } else if (value instanceof Collection) {
            Collection c = (Collection) value;
            StringBuffer sb = new StringBuffer("(");
            for (Iterator it = c.iterator(); it.hasNext(); ) {
                List single = new ArrayList();
                convert(it.next(), single, root);
                if (single.size() != 1) {
                    throw new IllegalStateException
                        ("can't deal with collection of compound objects");
                }
                sb.append(single.get(0));
                if (it.hasNext()) {
                    sb.append(",");
                } else {
                    sb.append(")");
                }
            }

            result.add(sb.toString());
        } else if (value instanceof String) {
            result.add(quote((String) value));
        } else if (value instanceof com.redhat.persistence.PropertyMap) {
            PropertyMap pmap = (PropertyMap) value;
            Collection props = Code.properties(pmap.getObjectType());
            for (Iterator it = props.iterator(); it.hasNext(); ) {
                Property prop = (Property) it.next();
                convert(pmap.get(prop), result, root);
            }
        } else {
            Adapter ad = root.getAdapter(value.getClass());
            convert(ad.getProperties(value), result, root);
        }
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
