package com.redhat.persistence.pdl.nodes;

/**
 * NestedMappingNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/18 $
 **/

public class NestedMappingNd extends Node {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/pdl/nodes/NestedMappingNd.java#2 $ by $Author: rhs $, $DateTime: 2004/08/18 14:57:34 $";

    public static final Field PATH =
        new Field(NestedMappingNd.class, "path", PathNd.class, 1, 1);
    public static final Field MAPPING =
        new Field(NestedMappingNd.class, "mapping", Node.class, 1, 1);
    public static final Field NESTED_MAP =
        new Field(NestedMapNd.class, "nestedMap", NestedMapNd.class, 0, 1);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onNestedMapping(this);
    }

    public PathNd getPath() {
        return (PathNd) get(PATH);
    }

    public Node getMapping() {
        return (Node) get(MAPPING);
    }

}
