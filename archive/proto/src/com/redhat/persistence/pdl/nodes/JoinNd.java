package com.redhat.persistence.pdl.nodes;

/**
 * Join
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/04 $
 **/

public class JoinNd extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/pdl/nodes/JoinNd.java#1 $ by $Author: dennis $, $DateTime: 2003/08/04 15:56:00 $";

    public static final Field FROM =
        new Field(JoinNd.class, "from", ColumnNd.class, 1, 1);
    public static final Field TO =
        new Field(JoinNd.class, "to", ColumnNd.class, 1, 1);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onJoin(this);
    }

    public ColumnNd getFrom() {
        return (ColumnNd) get(FROM);
    }

    public ColumnNd getTo() {
        return (ColumnNd) get(TO);
    }

}
