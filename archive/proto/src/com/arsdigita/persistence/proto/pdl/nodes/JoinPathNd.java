package com.arsdigita.persistence.proto.pdl.nodes;

import java.util.*;

/**
 * JoinPath
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/03/05 $
 **/

public class JoinPathNd extends StatementNd {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/JoinPathNd.java#3 $ by $Author: rhs $, $DateTime: 2003/03/05 18:41:57 $";

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
