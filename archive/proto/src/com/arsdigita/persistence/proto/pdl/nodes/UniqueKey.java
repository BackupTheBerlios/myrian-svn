package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * UniqueKey
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/31 $
 **/

public class UniqueKey extends Statement {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/UniqueKey.java#1 $ by $Author: rhs $, $DateTime: 2002/12/31 15:39:17 $";

    public static final Field PROPERTIES =
        new Field(UniqueKey.class, "properties", Identifier.class, 1);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onUniqueKey(this);
    }

}
