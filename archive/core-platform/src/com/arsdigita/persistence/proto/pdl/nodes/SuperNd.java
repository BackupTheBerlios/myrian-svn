package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * SuperNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/03 $
 **/

public class SuperNd extends Node {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/pdl/nodes/SuperNd.java#1 $ by $Author: rhs $, $DateTime: 2003/07/03 09:10:19 $";

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onSuper(this);
    }

}
