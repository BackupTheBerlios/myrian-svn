package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * Association
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/01/15 $
 **/

public class AssociationNd extends StatementNd {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/AssociationNd.java#1 $ by $Author: rhs $, $DateTime: 2003/01/15 10:39:47 $";

    public static final Field ROLE_ONE =
        new Field(AssociationNd.class, "roleOne", PropertyNd.class, 1, 1);
    public static final Field ROLE_TWO =
        new Field(AssociationNd.class, "roleTwo", PropertyNd.class, 1, 1);
    public static final Field PROPERTIES =
        new Field(AssociationNd.class, "properties", PropertyNd.class);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onAssociation(this);
    }

    public PropertyNd getRoleOne() {
        return (PropertyNd) get(ROLE_ONE);
    }

    public PropertyNd getRoleTwo() {
        return (PropertyNd) get(ROLE_TWO);
    }

}
