package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * DbType
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/01/15 $
 **/

public class DbTypeNd extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/DbTypeNd.java#1 $ by $Author: rhs $, $DateTime: 2003/01/15 10:39:47 $";

    public static final Field NAME =
        new Field(DbTypeNd.class, "name", IdentifierNd.class, 1, 1);

    private int m_size = -1;
    private int m_precision = -1;

    public void setSize(int size) {
        m_size = size;
    }

    public void setPrecision(int precision) {
        m_precision = precision;
    }

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onDbType(this);
    }

}
