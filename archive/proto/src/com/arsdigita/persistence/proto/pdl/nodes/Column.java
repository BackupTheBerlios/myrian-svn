package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * Column
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/31 $
 **/

public class Column extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/Column.java#1 $ by $Author: rhs $, $DateTime: 2002/12/31 15:39:17 $";

    public static final Field TABLE =
        new Field(Column.class, "table", Identifier.class, 1, 1);
    public static final Field COLUMN =
        new Field(Column.class, "column", Identifier.class, 1, 1);
    public static final Field TYPE =
        new Field(Column.class, "type", DbType.class, 0, 1);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onColumn(this);
    }

}
