package com.redhat.persistence.pdl.nodes;

/**
 * SuperNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public class SuperNd extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/pdl/nodes/SuperNd.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onSuper(this);
    }

}
