package com.redhat.persistence.pdl.nodes;

/**
 * NestedMapNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/08/18 $
 **/

public class NestedMapNd extends Node {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/pdl/nodes/NestedMapNd.java#1 $ by $Author: rhs $, $DateTime: 2004/08/18 14:57:34 $";

    public static final Field MAPPINGS =
        new Field(ObjectTypeNd.class, "mappings", NestedMappingNd.class);
    public static final Field OBJECT_KEY =
        new Field(ObjectTypeNd.class, "objectKey", ObjectKeyNd.class, 0, 1);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onNestedMap(this);
    }

}
