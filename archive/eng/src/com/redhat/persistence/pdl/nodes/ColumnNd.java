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

/**
 * Column
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class ColumnNd extends Node {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/pdl/nodes/ColumnNd.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
