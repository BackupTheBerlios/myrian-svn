package com.arsdigita.persistence.proto.pdl.nodes;

import java.util.*;

/**
 * AST
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/01/15 $
 **/

public class AST extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/AST.java#2 $ by $Author: rhs $, $DateTime: 2003/01/15 10:39:47 $";

    public static final Field FILES =
        new Field(AST.class, "files", FileNd.class);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onAST(this);
    }

}
