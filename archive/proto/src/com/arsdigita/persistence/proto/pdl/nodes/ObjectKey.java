package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * ObjectKey
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/31 $
 **/

public class ObjectKey extends Statement {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/ObjectKey.java#1 $ by $Author: rhs $, $DateTime: 2002/12/31 15:39:17 $";

    public static final Field PROPERTIES =
        new Field(ObjectKey.class, "properties", Identifier.class, 1);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onObjectKey(this);
    }

}
