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

/**
 * BindingNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/05/02 $
 **/

public class BindingNd extends Node {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/pdl/nodes/BindingNd.java#3 $ by $Author: rhs $, $DateTime: 2004/05/02 13:12:27 $";

    public static final Field PATH =
        new Field(BindingNd.class, "path", PathNd.class, 1, 1);
    public static final Field TYPE =
        new Field(BindingNd.class, "type", DbTypeNd.class, 1, 1);

    public PathNd getPath() {
        return (PathNd) get(PATH);
    }

    public DbTypeNd getType() {
        return (DbTypeNd) get(TYPE);
    }

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onBinding(this);
    }

}
