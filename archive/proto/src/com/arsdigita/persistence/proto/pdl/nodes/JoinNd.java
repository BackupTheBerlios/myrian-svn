package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * Join
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/01/15 $
 **/

public class JoinNd extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/JoinNd.java#2 $ by $Author: rhs $, $DateTime: 2003/01/15 17:57:03 $";

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
