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

import java.util.Collection;

/**
 * File
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class FileNd extends Node {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/pdl/nodes/FileNd.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    public static final Field MODEL =
        new Field(FileNd.class, "model", ModelNd.class, 1, 1);
    public static final Field IMPORTS =
        new Field(FileNd.class, "imports", ImportNd.class);
    public static final Field OBJECT_TYPES =
        new Field(FileNd.class, "objectTypes", ObjectTypeNd.class);
    public static final Field ASSOCIATIONS =
        new Field(FileNd.class, "associations", AssociationNd.class);
    public static final Field DATA_OPERATIONS =
        new Field(FileNd.class, "dataOperations", DataOperationNd.class);

    private String m_name;

    public FileNd(String name) {
        m_name = name;
    }

    public String getName() {
        return m_name;
    }

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onFile(this);
    }

    public FileNd getFile() {
        return this;
    }

    public ModelNd getModel() {
        return (ModelNd) get(MODEL);
    }

    public Collection getImports() {
        return (Collection) get(IMPORTS);
    }

}
