/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.redhat.persistence.pdl.nodes;

import java.util.Collection;

/**
 * UniqueKey
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/04/07 $
 **/

public class UniqueKeyNd extends StatementNd {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/pdl/nodes/UniqueKeyNd.java#5 $ by $Author: dennis $, $DateTime: 2004/04/07 16:07:11 $";

    public static final Field PROPERTIES =
        new Field(UniqueKeyNd.class, "properties", IdentifierNd.class, 1);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onUniqueKey(this);
    }

    public Collection getProperties() {
        return (Collection) get(PROPERTIES);
    }

}
