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
 * Join
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/04/07 $
 **/

public class JoinNd extends Node {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/pdl/nodes/JoinNd.java#4 $ by $Author: dennis $, $DateTime: 2004/04/07 16:07:11 $";

    public static final Field FROM =
        new Field(JoinNd.class, "from", ColumnNd.class, 1, 1);
    public static final Field TO =
        new Field(JoinNd.class, "to", ColumnNd.class, 1, 1);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onJoin(this);
    }

    public ColumnNd getFrom() {
        return (ColumnNd) get(FROM);
    }

    public ColumnNd getTo() {
        return (ColumnNd) get(TO);
    }

}
