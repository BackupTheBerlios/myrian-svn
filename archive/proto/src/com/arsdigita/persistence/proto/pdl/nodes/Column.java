package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * Column
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/01/15 $
 **/

public class Column extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/Column.java#2 $ by $Author: rhs $, $DateTime: 2003/01/15 09:35:55 $";

    public static final Field TABLE =
        new Field(Column.class, "table", Identifier.class, 1, 1);
    public static final Field NAME =
        new Field(Column.class, "name", Identifier.class, 1, 1);
    public static final Field TYPE =
        new Field(Column.class, "type", DbType.class, 0, 1);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onColumn(this);
    }

    public Identifier getTable() {
        return (Identifier) get(TABLE);
    }

    public Identifier getName() {
        return (Identifier) get(NAME);
    }

    public DbType getType() {
        return (DbType) get(TYPE);
    }

}
