package com.redhat.persistence.oql;

// XXX: dependency on c.a.db.DbHelper
import com.arsdigita.db.DbHelper;
import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Literal
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #16 $ $Date: 2004/03/16 $
 **/

public class Literal extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Literal.java#16 $ by $Author: rhs $, $DateTime: 2004/03/16 17:36:07 $";

    private Object m_value;

    public Literal(Object value) {
        m_value = value;
    }

    void frame(Generator gen) {
        QFrame frame = gen.frame(this, null);
        List result = new ArrayList();
        convert(m_value, result, gen.getRoot());
        if (result.size() == 0) {
            throw new IllegalStateException
                ("unable to convert value: " + m_value);
        }
        List values = new ArrayList();
        for (int i = 0; i < result.size(); i++) {
            Code c = (Code) result.get(i);
            QValue v = frame.getValue(c);
            values.add(v);
            if (c.isNull()) {
                gen.addNull(this, v);
            } else {
                gen.addNonNull(this, v);
            }
        }
        frame.setValues(values);
    }

    Code emit(Generator gen) {
        return gen.getFrame(this).emit();
    }

    private static void convert(Object value, List result, Root root) {
        if (value == null) {
            result.add(Code.NULL);
        } else if (value instanceof Collection) {
            Collection c = (Collection) value;
            Code sql = new Code("(");
            for (Iterator it = c.iterator(); it.hasNext(); ) {
                List single = new ArrayList();
                convert(it.next(), single, root);
                if (single.size() != 1) {
                    throw new IllegalStateException
                        ("can't deal with collection of compound objects");
                }
                sql = sql.add((Code) single.get(0));
                if (it.hasNext()) {
                    sql = sql.add(",");
                } else {
                    sql = sql.add(")");
                }
            }

            result.add(sql);
        } else {
            Adapter ad = root.getAdapter(value.getClass());
            PropertyMap pmap = ad.getProperties(value);
            if (pmap.getObjectType().isCompound()) {
                convert(pmap, result, root);
            } else {
                Code.Binding b = new Code.Binding(value, ad.defaultJDBCType());
                result.add(new Code("?", Collections.singletonList(b)));
            }
        }
    }

    private static void convert(PropertyMap pmap, List result, Root root) {
        Collection props = Code.properties(pmap.getObjectType());
        for (Iterator it = props.iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            convert(pmap.get(prop), result, root);
        }
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
