package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * Statement
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public abstract class StatementNd extends Node {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/pdl/nodes/StatementNd.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onStatement(this);
    }
}
