package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * Path
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/31 $
 **/

public class Path extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/Path.java#1 $ by $Author: rhs $, $DateTime: 2002/12/31 15:39:17 $";

    public static final Field PATH =
        new Field(Path.class, "path", Identifier.class, 1);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onPath(this);
    }

}
