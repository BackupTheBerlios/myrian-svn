package com.arsdigita.persistence.proto.pdl.nodes;

import java.util.*;

/**
 * ObjectKey
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/01/30 $
 **/

public class ObjectKeyNd extends StatementNd {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/ObjectKeyNd.java#2 $ by $Author: rhs $, $DateTime: 2003/01/30 17:57:25 $";

    public static final Field PROPERTIES =
        new Field(ObjectKeyNd.class, "properties", IdentifierNd.class, 1);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onObjectKey(this);
    }

    public Collection getProperties() {
        return (Collection) get(PROPERTIES);
    }

}
