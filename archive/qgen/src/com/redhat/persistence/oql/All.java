package com.redhat.persistence.oql;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * All
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2004/02/24 $
 **/

public class All extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/All.java#7 $ by $Author: rhs $, $DateTime: 2004/02/24 19:43:59 $";

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
        ObjectMap map = type.getRoot().getObjectMap(type);
        SQLBlock block = map.getRetrieveAll();
        String[] columns = Code.columns(type, null);

        if (block == null) {
            QFrame frame = gen.frame(this, type);
            frame.setTable(Code.table(map).getName());
            frame.setValues(columns);
        } else if (columns.length > 0) {
            QFrame frame = gen.frame(this, type);
            Static all = new Static
                (block.getSQL(), m_type, columns, false, m_bindings);
            all.frame(gen);
            QFrame child = gen.getFrame(all);
            frame.addChild(child);
            frame.setValues(child.getValues());
        } else {
            Static all = new Static
                (block.getSQL(), null, null, false, m_bindings);
            all.frame(gen);
            gen.setSubstitute(this, all);
        }
    }

    String emit(Generator gen) {
        Expression sub = gen.getSubstitute(this);
        if (sub != null) {
            return sub.emit(gen);
        }
        return gen.getFrame(this).emit();
    }

    public String toString() {
        return "all(" + m_type + ")";
    }

    String summary() {
        return "all: " + m_type;
    }

}
