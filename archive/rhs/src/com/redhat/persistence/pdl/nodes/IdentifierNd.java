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
 * Identifier
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/11/09 $
 **/

public class IdentifierNd extends Node {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/pdl/nodes/IdentifierNd.java#1 $ by $Author: rhs $, $DateTime: 2003/11/09 14:41:17 $";

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
