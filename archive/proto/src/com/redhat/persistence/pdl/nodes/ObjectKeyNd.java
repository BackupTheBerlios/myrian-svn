package com.redhat.persistence.pdl.nodes;

import java.util.*;

/**
 * ObjectKey
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public class ObjectKeyNd extends StatementNd {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/pdl/nodes/ObjectKeyNd.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

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
