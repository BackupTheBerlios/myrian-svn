package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * BindingNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/02/26 $
 **/

public class BindingNd extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/BindingNd.java#2 $ by $Author: rhs $, $DateTime: 2003/02/26 12:01:31 $";

    public static final Field PATH =
        new Field(BindingNd.class, "path", PathNd.class, 1, 1);
    public static final Field TYPE =
        new Field(BindingNd.class, "type", DbTypeNd.class, 1, 1);

    public PathNd getPath() {
        return (PathNd) get(PATH);
    }

    public DbTypeNd getType() {
        return (DbTypeNd) get(TYPE);
    }

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onBinding(this);
    }

}
