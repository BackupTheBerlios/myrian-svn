/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.redhat.persistence.pdl.nodes;

/**
 * Import
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class ImportNd extends Node {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/pdl/nodes/ImportNd.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
