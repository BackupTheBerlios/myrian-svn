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
 * Import
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/06/07 $
 **/

public class ImportNd extends Node {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/pdl/nodes/ImportNd.java#1 $ by $Author: rhs $, $DateTime: 2004/06/07 13:49:55 $";

    public static final Field PATH =
        new Field(ImportNd.class, "path", IdentifierNd.class, 1);

    private boolean m_isWildcard = false;

    public void setWildcard(boolean value) {
        m_isWildcard = value;
    }

    public boolean isWildcard() {
        return m_isWildcard;
    }

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onImport(this);
    }

    private String getPath() {
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

    public String qualify(TypeNd type) {
        if (type.isQualified()) {
            throw new IllegalArgumentException
                ("Type is already qualified.");
        }

        if (isWildcard()) {
            return getPath() + "." + type.getName();
        } else if (getPath().endsWith("." + type.getName())) {
            return getPath();
        } else {
            return null;
        }
    }

}
