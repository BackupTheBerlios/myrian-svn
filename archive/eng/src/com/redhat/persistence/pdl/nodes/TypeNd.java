/*
 * Copyright (C) 2003-2004 Red Hat, Inc. All Rights Reserved.
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

import java.util.*;

/**
 * Type
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/09/22 $
 **/

public class TypeNd extends Node {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/pdl/nodes/TypeNd.java#4 $ by $Author: rhs $, $DateTime: 2004/09/22 15:20:55 $";

    public static final Field IDENTIFIERS =
        new Field(TypeNd.class, "identifiers", IdentifierNd.class, 1);
    public static final Field ARGUMENTS =
        new Field(TypeNd.class, "arguments", TypeNd.class);

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

    public List getArguments() {
        return (List) get(ARGUMENTS);
    }

    public String getQualifiedName() {
        if (!isQualified()) {
            throw new IllegalArgumentException
                ("Not a qualified type");
        }
        StringBuffer result = new StringBuffer();
        for (Iterator it = getIdentifiers().iterator(); it.hasNext(); ) {
            IdentifierNd id = (IdentifierNd) it.next();
            result.append(id.getName());
            if (it.hasNext()) {
                result.append('.');
            }
        }
        return result.toString();
    }

}
