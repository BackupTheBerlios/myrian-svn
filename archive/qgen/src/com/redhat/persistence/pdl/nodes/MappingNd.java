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
 * MappingNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/10 $
 **/

public class MappingNd extends Node {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/pdl/nodes/MappingNd.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

    public static final Field PATH =
        new Field(MappingNd.class, "path", PathNd.class, 1, 1);
    public static final Field COLUMN =
        new Field(MappingNd.class, "column", PathNd.class, 1, 1);

    public PathNd getPath() {
        return (PathNd) get(PATH);
    }

    public PathNd getCol() {
        return (PathNd) get(COLUMN);
    }

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onMapping(this);
    }

}
