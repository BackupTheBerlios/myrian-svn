/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.redhat.persistence.pdl.nodes;

import java.util.Collection;

/**
 * Association
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/11/09 $
 **/

public class AssociationNd extends StatementNd {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/pdl/nodes/AssociationNd.java#1 $ by $Author: rhs $, $DateTime: 2003/11/09 14:41:17 $";

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
