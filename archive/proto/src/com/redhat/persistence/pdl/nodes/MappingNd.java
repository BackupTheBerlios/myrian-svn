package com.redhat.persistence.pdl.nodes;

/**
 * MappingNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public class MappingNd extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/pdl/nodes/MappingNd.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    public static final Field PATH =
        new Field(MappingNd.class, "path", PathNd.class, 1, 1);
    public static final Field COLUMN =
        new Field(MappingNd.class, "column", PathNd.class, 1, 1);

    public PathNd getPath() {
        return (PathNd) get(PATH);
    }

    public PathNd getCol() {
        return (PathNd) get(COLUMN);
    }

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onMapping(this);
    }

}
