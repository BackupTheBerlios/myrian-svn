package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * ReferenceKey
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/31 $
 **/

public class ReferenceKey extends Statement {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/ReferenceKey.java#1 $ by $Author: rhs $, $DateTime: 2002/12/31 15:39:17 $";

    public static final Field COLUMN =
        new Field(ReferenceKey.class, "column", Column.class, 1, 1);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onReferenceKey(this);
    }

}
