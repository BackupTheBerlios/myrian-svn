package com.redhat.persistence.pdl.nodes;

/**
 * Column
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public class ColumnNd extends Node {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/pdl/nodes/ColumnNd.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    public static final Field TABLE =
        new Field(ColumnNd.class, "table", IdentifierNd.class, 1, 1);
    public static final Field NAME =
        new Field(ColumnNd.class, "name", IdentifierNd.class, 1, 1);
    public static final Field TYPE =
        new Field(ColumnNd.class, "type", DbTypeNd.class, 0, 1);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onColumn(this);
    }

    public IdentifierNd getTable() {
        return (IdentifierNd) get(TABLE);
    }

    public IdentifierNd getName() {
        return (IdentifierNd) get(NAME);
    }

    public DbTypeNd getType() {
        return (DbTypeNd) get(TYPE);
    }

}
