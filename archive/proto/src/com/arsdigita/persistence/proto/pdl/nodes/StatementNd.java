package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * Statement
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/01/15 $
 **/

public abstract class StatementNd extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/StatementNd.java#1 $ by $Author: rhs $, $DateTime: 2003/01/15 10:39:47 $";

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onStatement(this);
    }
}
