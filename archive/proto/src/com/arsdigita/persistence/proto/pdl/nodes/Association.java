package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * Association
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/31 $
 **/

public class Association extends Statement {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/Association.java#1 $ by $Author: rhs $, $DateTime: 2002/12/31 15:39:17 $";

    public static final Field ROLE_ONE =
        new Field(Association.class, "roleOne", Property.class, 1, 1);
    public static final Field ROLE_TWO =
        new Field(Association.class, "roleTwo", Property.class, 1, 1);
    public static final Field PROPERTIES =
        new Field(Association.class, "properties", Property.class);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onAssociation(this);
    }

    public Property getRoleOne() {
        return (Property) get(ROLE_ONE);
    }

    public Property getRoleTwo() {
        return (Property) get(ROLE_TWO);
    }

}
