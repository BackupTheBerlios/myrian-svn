package com.redhat.persistence.pdl.nodes;

import java.util.*;

/**
 * AST
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public class AST extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/pdl/nodes/AST.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    public static final Field FILES =
        new Field(AST.class, "files", FileNd.class);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onAST(this);
    }

}
