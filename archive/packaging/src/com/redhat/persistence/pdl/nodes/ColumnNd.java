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

/**
 * Column
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/19 $
 **/

public class ColumnNd extends Node {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/pdl/nodes/ColumnNd.java#2 $ by $Author: rhs $, $DateTime: 2003/08/19 22:28:24 $";

    public static final Field TABLE =
        new Field(ColumnNd.class, "table", IdentifierNd.class, 1, 1);
    public static final Field NAME =
        new Field(ColumnNd.class, "name", IdentifierNd.class, 1, 1);
    public static final Field TYPE =
        new Field(ColumnNd.class, "type", DbTypeNd.class, 0, 1);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onColumn(this);
    }

    public IdentifierNd getTable() {
        return (IdentifierNd) get(TABLE);
    }

    public IdentifierNd getName() {
        return (IdentifierNd) get(NAME);
    }

    public DbTypeNd getType() {
        return (DbTypeNd) get(TYPE);
    }

}