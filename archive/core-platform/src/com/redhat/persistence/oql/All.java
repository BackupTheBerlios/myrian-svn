package com.redhat.persistence.oql;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * All
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/03/28 $
 **/

public class All extends Expression {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/All.java#3 $ by $Author: rhs $, $DateTime: 2004/03/28 22:52:45 $";

    private String m_type;
    private Map m_bindings;
    private Expression m_scope;
    private boolean m_substitute;

    public All(String type) {
        this(type, Collections.EMPTY_MAP, null, false);
    }

    All(String type, Map bindings, Expression scope, boolean substitute) {
        m_type = type;
        m_bindings = bindings;
        m_scope = scope == null ? this : scope;
        m_substitute = substitute;
    }

    String getType() {
        return m_type;
    }

    void frame(Generator gen) {
        final ObjectType type = gen.getType(m_type);
        ObjectMap map = type.getRoot().getObjectMap(type);
        SQLBlock block = map.getRetrieveAll();
        String[] columns = Code.columns(type, null);

        if (block == null) {
            QFrame frame = gen.frame(this, type);
            frame.setTable(Code.table(map).getName());
            frame.setValues(columns);
        } else if (m_substitute || gen.isBoolean(this)) {
            Static all = new Static
                (block.getSQL(), null, false, m_bindings, m_scope);
            all.frame(gen);
            gen.setSubstitute(this, all);
        } else {
            QFrame frame = gen.frame(this, type);
            Static all = new Static
                (block.getSQL(), columns, false, m_bindings, m_scope) {
                protected ObjectType getType() { return type; }
                protected boolean hasType() { return true; }
            };
            all.frame(gen);
            QFrame child = gen.getFrame(all);
            frame.addChild(child);
            frame.setValues(child.getValues());
            for (Iterator it = block.getPaths().iterator(); it.hasNext(); ) {
                Path p = (Path) it.next();
                frame.addMapping(p, block.getMapping(p).getPath());
            }
        }
    }

    Code emit(Generator gen) {
        Expression sub = gen.getSubstitute(this);
        if (sub != null) {
            return sub.emit(gen);
        }
        return gen.getFrame(this).emit();
    }

    void hash(Generator gen) {
        ObjectType type = gen.getType(m_type);
        gen.hash(type);
        gen.hash(getClass());
    }

    public String toString() {
        return "all(" + m_type + ")";
    }

    String summary() {
        return "all: " + m_type;
    }

}
