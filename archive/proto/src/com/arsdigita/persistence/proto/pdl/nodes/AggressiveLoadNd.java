package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * AggressiveLoad
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/01/15 $
 **/

public class AggressiveLoadNd extends StatementNd {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/AggressiveLoadNd.java#1 $ by $Author: rhs $, $DateTime: 2003/01/15 10:39:47 $";

    public static final Field PATHS =
        new Field(AggressiveLoadNd.class, "paths", PathNd.class, 1);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onAggressiveLoad(this);
    }

}
