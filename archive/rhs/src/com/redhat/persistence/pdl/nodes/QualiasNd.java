/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
 * QualiasNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/05/02 $
 **/

public class QualiasNd extends Node {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/pdl/nodes/QualiasNd.java#2 $ by $Author: rhs $, $DateTime: 2004/05/02 13:12:27 $";

    private String m_query;

    public QualiasNd(String query) {
        m_query = query;
    }

    public String getQuery() {
        return m_query;
    }

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onQualias(this);
    }

}
