package com.arsdigita.persistence.proto.pdl.nodes;

import java.util.*;

/**
 * AST
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/31 $
 **/

public class AST extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/AST.java#1 $ by $Author: rhs $, $DateTime: 2002/12/31 15:39:17 $";

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onAST(this);
    }

}
