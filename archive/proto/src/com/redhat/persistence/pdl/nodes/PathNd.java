package com.redhat.persistence.pdl.nodes;

import com.redhat.persistence.common.*;

/**
 * Path
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/04 $
 **/

public class PathNd extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/pdl/nodes/PathNd.java#1 $ by $Author: dennis $, $DateTime: 2003/08/04 15:56:00 $";

    public static final Field PATH =
        new Field(PathNd.class, "path", IdentifierNd.class, 1);

    public Path getPath() {
        final StringBuffer result = new StringBuffer();
        traverse(new Switch() {
                public void onIdentifier(IdentifierNd id) {
                    if (result.length() > 0) {
                        result.append('.');
                    }
                    result.append(id.getName());
                }
            });
        return Path.get(result.toString());
    }

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onPath(this);
    }

}
