package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * Statement
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/31 $
 **/

public abstract class Statement extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/Statement.java#1 $ by $Author: rhs $, $DateTime: 2002/12/31 15:39:17 $";

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onStatement(this);
    }
}
