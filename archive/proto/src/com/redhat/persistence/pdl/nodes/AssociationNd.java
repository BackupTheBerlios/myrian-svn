package com.redhat.persistence.pdl.nodes;

import java.util.*;

/**
 * Association
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/04 $
 **/

public class AssociationNd extends StatementNd {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/pdl/nodes/AssociationNd.java#1 $ by $Author: dennis $, $DateTime: 2003/08/04 15:56:00 $";

    public static final Field ROLE_ONE =
        new Field(AssociationNd.class, "roleOne", PropertyNd.class, 1, 1);
    public static final Field ROLE_TWO =
        new Field(AssociationNd.class, "roleTwo", PropertyNd.class, 1, 1);
    public static final Field PROPERTIES =
        new Field(AssociationNd.class, "properties", PropertyNd.class);
    public static final Field EVENTS =
        new Field(AssociationNd.class, "events", EventNd.class);

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

    public Collection getProperties() {
	return (Collection) get(PROPERTIES);
    }

}
