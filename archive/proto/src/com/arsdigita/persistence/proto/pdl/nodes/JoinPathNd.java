package com.arsdigita.persistence.proto.pdl.nodes;

import java.util.*;

/**
 * JoinPath
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/01/15 $
 **/

public class JoinPathNd extends StatementNd {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/JoinPathNd.java#2 $ by $Author: rhs $, $DateTime: 2003/01/15 17:57:03 $";

    public static final Field JOINS =
        new Field(JoinPathNd.class, "joins", JoinNd.class, 1, 2);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onJoinPath(this);
    }

    public Collection getJoins() {
        return (Collection) get(JOINS);
    }

}
