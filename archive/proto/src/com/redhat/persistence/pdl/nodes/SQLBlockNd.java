package com.redhat.persistence.pdl.nodes;

import java.util.*;

/**
 * SQLBlockNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public class SQLBlockNd extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/pdl/nodes/SQLBlockNd.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    public static final Field MAPPINGS =
        new Field(SQLBlockNd.class, "mappings", MappingNd.class, 0);
    public static final Field BINDINGS =
        new Field(SQLBlockNd.class, "bindings", BindingNd.class, 0);

    private String m_sql;

    public SQLBlockNd(String sql) {
        m_sql = sql;
    }

    public String getSQL() {
        return m_sql;
    }

    public Collection getMappings() {
        return (Collection) get(MAPPINGS);
    }

    public Collection getBindings() {
        return (Collection) get(BINDINGS);
    }

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onSQLBlock(this);
    }

}
