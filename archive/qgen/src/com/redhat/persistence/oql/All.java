package com.redhat.persistence.oql;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * All
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/02/21 $
 **/

public class All extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/All.java#5 $ by $Author: rhs $, $DateTime: 2004/02/21 18:22:56 $";

    private String m_type;
    private Map m_bindings;

    public All(String type) {
        this(type, Collections.EMPTY_MAP);
    }

    All(String type, Map bindings) {
        m_type = type;
        m_bindings = bindings;
    }

    String getType() {
        return m_type;
    }

    void frame(Generator gen) {
        ObjectType type = gen.getType(m_type);
        QFrame frame = gen.frame(this, type);
        ObjectMap map = type.getRoot().getObjectMap(type);
        SQLBlock block = map.getRetrieveAll();
        String[] columns = Code.columns(frame.getType(), null);
        if (block == null) {
            frame.setTable(map.getTable().getName());
            frame.setValues(columns);
        } else {
            SQL sql = block.getSQL();
            Static all = new Static
                (sql, m_type, Code.columns(type, null), false, m_bindings);
            all.frame(gen);
            QFrame child = gen.getFrame(all);
            frame.addChild(child);
            frame.setValues(child.getValues());
        }
    }

    String emit(Generator gen) {
        return gen.getFrame(this).emit();
    }

    public String toString() {
        return "all(" + m_type + ")";
    }

    String summary() {
        return "all: " + m_type;
    }

}
