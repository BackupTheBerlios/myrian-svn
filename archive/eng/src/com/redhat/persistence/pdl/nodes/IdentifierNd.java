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

/**
 * Identifier
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/09/07 $
 **/

public class IdentifierNd extends Node {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/pdl/nodes/IdentifierNd.java#3 $ by $Author: dennis $, $DateTime: 2004/09/07 10:26:15 $";

    private String m_name;

    public IdentifierNd(String name) {
        m_name = name;
    }

    public String getName() {
        return m_name;
    }

    public String toString() {
        return m_name;
    }

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onIdentifier(this);
    }

}
