package com.redhat.persistence.oql;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * All
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/02/21 $
 **/

public class All extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/All.java#4 $ by $Author: rhs $, $DateTime: 2004/02/21 13:11:19 $";

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
        //frame.setValues(Code.columns(frame.getType(), null));
        //frame.setTable(Code.table(frame.getType()));
    }

    String emit(Generator gen) {
        return gen.getFrame(this).emit();
    }

    void graph(Pane pane) {
        throw new UnsupportedOperationException();
    }

    Code.Frame frame(Code code) {
        ObjectType type = code.getType(m_type);
        if (type == null) {
            throw new IllegalStateException("no such type: " + m_type);
        }
        Code.Frame frame = code.frame(code.getType(m_type));
        String alias = frame.alias(frame.type);
        code.setTable(alias, code.table(frame.type));
        code.setAlias(this, alias);
        code.setFrame(this, frame);
        return frame;
    }

    void opt(Code code) {}

    void emit(Code code) {
        Code.Frame frame = code.getFrame(this);
        String join = frame.join();
        if (join != null) {
            code.append(join);
        } else {
            code.append("(select 1) " + code.var("d"));
        }

        /*String alias = code.getAlias(this);
        code.append(code.table(frame.type));
        code.append(" ");
        code.append(alias);*/
    }

    public String toString() {
        return "all(" + m_type + ")";
    }

    String summary() {
        return "all: " + m_type;
    }

}
