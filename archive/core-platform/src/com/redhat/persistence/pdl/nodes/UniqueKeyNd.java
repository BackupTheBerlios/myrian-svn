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
 * UniqueKey
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/10/28 $
 **/

public class UniqueKeyNd extends StatementNd {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/pdl/nodes/UniqueKeyNd.java#3 $ by $Author: jorris $, $DateTime: 2003/10/28 18:36:21 $";

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
