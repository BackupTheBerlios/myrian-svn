package com.redhat.persistence.pdl.nodes;

/**
 * BindingNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/04 $
 **/

public class BindingNd extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/pdl/nodes/BindingNd.java#1 $ by $Author: dennis $, $DateTime: 2003/08/04 15:56:00 $";

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
