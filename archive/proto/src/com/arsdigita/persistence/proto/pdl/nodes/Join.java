package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * Join
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/31 $
 **/

public class Join extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/Join.java#1 $ by $Author: rhs $, $DateTime: 2002/12/31 15:39:17 $";

    public static final Field FROM =
        new Field(Join.class, "from", Column.class, 1, 1);
    public static final Field TO =
        new Field(Join.class, "to", Column.class, 1, 1);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onJoin(this);
    }

}
