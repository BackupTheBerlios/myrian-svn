package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * JoinPath
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/31 $
 **/

public class JoinPath extends Statement {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/JoinPath.java#1 $ by $Author: rhs $, $DateTime: 2002/12/31 15:39:17 $";

    public static final Field JOINS =
        new Field(JoinPath.class, "joins", Join.class, 1, 2);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onJoinPath(this);
    }

}
