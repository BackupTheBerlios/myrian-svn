package com.arsdigita.persistence.proto.pdl.nodes;

import java.util.*;

/**
 * AST
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class AST extends Node {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/pdl/nodes/AST.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    public static final Field FILES =
        new Field(AST.class, "files", FileNd.class);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onAST(this);
    }

}