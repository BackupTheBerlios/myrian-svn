package com.redhat.persistence.pdl.nodes;

import java.util.*;

/**
 * AST
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public class AST extends Node {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/pdl/nodes/AST.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    public static final Field FILES =
        new Field(AST.class, "files", FileNd.class);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onAST(this);
    }

}