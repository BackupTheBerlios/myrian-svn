package com.arsdigita.persistence.proto.pdl.nodes;

import java.util.*;

/**
 * UniqueKey
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class UniqueKeyNd extends StatementNd {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/pdl/nodes/UniqueKeyNd.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    public static final Field PROPERTIES =
        new Field(UniqueKeyNd.class, "properties", IdentifierNd.class, 1);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onUniqueKey(this);
    }

    public Collection getProperties() {
        return (Collection) get(PROPERTIES);
    }

}
