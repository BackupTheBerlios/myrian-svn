/*
 * Copyright (C) 2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.redhat.persistence.pdl.nodes;

/**
 * NestedMappingNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/09/07 $
 **/

public class NestedMappingNd extends Node {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/pdl/nodes/NestedMappingNd.java#4 $ by $Author: dennis $, $DateTime: 2004/09/07 10:26:15 $";

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
