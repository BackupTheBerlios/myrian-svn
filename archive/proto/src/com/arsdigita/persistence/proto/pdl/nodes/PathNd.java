package com.arsdigita.persistence.proto.pdl.nodes;

import com.arsdigita.persistence.proto.common.*;

/**
 * Path
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/02/26 $
 **/

public class PathNd extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/PathNd.java#2 $ by $Author: rhs $, $DateTime: 2003/02/26 12:01:31 $";

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
