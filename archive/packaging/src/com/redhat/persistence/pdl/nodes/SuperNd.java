package com.redhat.persistence.pdl.nodes;

/**
 * SuperNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 **/

public class SuperNd extends Node {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/pdl/nodes/SuperNd.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onSuper(this);
    }

}
