package com.arsdigita.persistence.proto.metadata;

import com.arsdigita.persistence.proto.common.*;
import java.util.*;

/**
 * SQLBlock
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/02/19 $
 **/

public class SQLBlock {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/SQLBlock.java#1 $ by $Author: rhs $, $DateTime: 2003/02/19 22:58:51 $";

    private String m_sql;
    private HashMap m_mappings = new HashMap();
    private HashMap m_bindings = new HashMap();

    public SQLBlock(String sql) {
        m_sql = sql;
    }

    public String getSQL() {
        return m_sql;
    }

    public boolean hasMapping(Path path) {
        return m_mappings.containsKey(path);
    }

    public void addMapping(Path path, Path column) {
        if (hasMapping(path)) {
            throw new IllegalArgumentException
                ("already have mapping: " + path);
        }
        m_mappings.put(path, column);
    }

    public Path getMapping(Path path) {
        return (Path) m_mappings.get(path);
    }

    public boolean hasBinding(Path path) {
        return m_bindings.containsKey(path);
    }

    public void addBinding(Path path, int type) {
        if (hasBinding(path)) {
            throw new IllegalArgumentException
                ("already have mapping: " + path);
        }
        m_bindings.put(path, new Integer(type));
    }

    public int getBinding(Path path) {
        return ((Integer) m_bindings.get(path)).intValue();
    }

}
