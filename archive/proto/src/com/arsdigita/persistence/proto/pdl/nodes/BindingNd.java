package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * BindingNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/02/19 $
 **/

public class BindingNd extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/BindingNd.java#1 $ by $Author: rhs $, $DateTime: 2003/02/19 10:14:49 $";

    public static final Field PATH =
        new Field(BindingNd.class, "path", PathNd.class, 1, 1);
    public static final Field TYPE =
        new Field(BindingNd.class, "type", DbTypeNd.class, 1, 1);

}
