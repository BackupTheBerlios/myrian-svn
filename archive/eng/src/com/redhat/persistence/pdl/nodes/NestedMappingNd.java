/*
 * Copyright (C) 2004 Red Hat, Inc.  All Rights Reserved.
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
 * NestedMappingNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/08/30 $
 **/

public class NestedMappingNd extends Node {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/pdl/nodes/NestedMappingNd.java#3 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    public static final Field PATH =
        new Field(NestedMappingNd.class, "path", PathNd.class, 1, 1);
    public static final Field MAPPING =
        new Field(NestedMappingNd.class, "mapping", Node.class, 1, 1);
    public static final Field NESTED_MAP =
        new Field(NestedMapNd.class, "nestedMap", NestedMapNd.class, 0, 1);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onNestedMapping(this);
    }

    public PathNd getPath() {
        return (PathNd) get(PATH);
    }

    public Node getMapping() {
        return (Node) get(MAPPING);
    }

}
