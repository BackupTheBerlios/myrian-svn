package com.redhat.persistence.pdl.nodes;

/**
 * SuperNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/04 $
 **/

public class SuperNd extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/pdl/nodes/SuperNd.java#1 $ by $Author: dennis $, $DateTime: 2003/08/04 15:56:00 $";

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onSuper(this);
    }

}
