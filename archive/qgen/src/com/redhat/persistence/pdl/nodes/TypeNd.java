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
 * Type
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/10 $
 **/

public class TypeNd extends Node {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/pdl/nodes/TypeNd.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

    public static final Field IDENTIFIERS =
        new Field(TypeNd.class, "identifiers", IdentifierNd.class, 1);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onType(this);
    }

    private Collection getIdentifiers() {
        return (Collection) get(IDENTIFIERS);
    }

    public boolean isQualified() {
        return getIdentifiers().size() > 1;
    }

    public String getName() {
        return ((IdentifierNd) getIdentifiers().iterator().next()).getName();
    }

    public String getQualifiedName() {
        if (!isQualified()) {
            throw new IllegalArgumentException
                ("Not a qualified type");
        }
        final StringBuffer result = new StringBuffer();
        traverse(new Switch() {
                public void onIdentifier(IdentifierNd id) {
                    if (result.length() > 0) {
                        result.append('.');
                    }
                    result.append(id.getName());
                }
            });
        return result.toString();
    }

}
