package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * DataOperationNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class DataOperationNd extends Node {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/pdl/nodes/DataOperationNd.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    public static final Field NAME =
        new Field(DataOperationNd.class, "name", IdentifierNd.class, 1, 1);
    public static final Field SQL =
        new Field(DataOperationNd.class, "sql", SQLBlockNd.class, 1, 1);

    public void dispatch(Switch sw) {
        sw.onDataOperation(this);
    }

    public IdentifierNd getName() {
        return (IdentifierNd) get(NAME);
    }

    public SQLBlockNd getSQL() {
        return (SQLBlockNd) get(SQL);
    }

}
