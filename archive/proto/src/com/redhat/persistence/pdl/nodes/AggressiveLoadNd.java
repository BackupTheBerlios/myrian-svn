package com.redhat.persistence.pdl.nodes;

/**
 * AggressiveLoad
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/04 $
 **/

public class AggressiveLoadNd extends StatementNd {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/pdl/nodes/AggressiveLoadNd.java#1 $ by $Author: dennis $, $DateTime: 2003/08/04 15:56:00 $";

    public static final Field PATHS =
        new Field(AggressiveLoadNd.class, "paths", PathNd.class, 1);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onAggressiveLoad(this);
    }

}
