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
package org.myrian.persistence.pdl.nodes;

/**
 * NestedMapNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/10/04 $
 **/

public class NestedMapNd extends Node {


    public static final Field MAPPINGS =
        new Field(ObjectTypeNd.class, "mappings", NestedMappingNd.class);
    public static final Field OBJECT_KEY =
        new Field(ObjectTypeNd.class, "objectKey", ObjectKeyNd.class, 0, 1);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onNestedMap(this);
    }

}
