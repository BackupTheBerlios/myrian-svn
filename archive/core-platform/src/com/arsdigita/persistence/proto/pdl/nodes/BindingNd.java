package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * BindingNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class BindingNd extends Node {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/pdl/nodes/BindingNd.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

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
