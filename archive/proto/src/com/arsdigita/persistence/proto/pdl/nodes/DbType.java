package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * DbType
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/31 $
 **/

public class DbType extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/DbType.java#1 $ by $Author: rhs $, $DateTime: 2002/12/31 15:39:17 $";

    public static final Field NAME =
        new Field(DbType.class, "name", Identifier.class, 1, 1);

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
