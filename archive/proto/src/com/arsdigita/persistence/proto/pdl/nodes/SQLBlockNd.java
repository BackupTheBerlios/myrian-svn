package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * SQLBlockNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/02/19 $
 **/

public class SQLBlockNd extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/SQLBlockNd.java#1 $ by $Author: rhs $, $DateTime: 2003/02/19 10:14:49 $";

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

}
