package com.arsdigita.persistence.proto.pdl.nodes;

import java.util.*;

/**
 * JoinPath
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class JoinPathNd extends StatementNd {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/pdl/nodes/JoinPathNd.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    public static final Field JOINS =
        new Field(JoinPathNd.class, "joins", JoinNd.class, 1, 2);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onJoinPath(this);
    }

    public List getJoins() {
        return (List) get(JOINS);
    }

}
