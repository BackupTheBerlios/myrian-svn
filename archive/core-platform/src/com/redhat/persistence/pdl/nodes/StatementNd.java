package com.redhat.persistence.pdl.nodes;

/**
 * Statement
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public abstract class StatementNd extends Node {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/pdl/nodes/StatementNd.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onStatement(this);
    }
}
