/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.redhat.persistence.pdl.nodes;

import java.util.Collection;

/**
 * Association
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class AssociationNd extends StatementNd {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/pdl/nodes/AssociationNd.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
