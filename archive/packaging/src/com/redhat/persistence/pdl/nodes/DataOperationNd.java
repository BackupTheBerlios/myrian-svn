package com.redhat.persistence.pdl.nodes;

/**
 * DataOperationNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 **/

public class DataOperationNd extends Node {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/pdl/nodes/DataOperationNd.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

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
