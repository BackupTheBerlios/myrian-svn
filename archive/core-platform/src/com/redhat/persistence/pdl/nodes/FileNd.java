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

import java.util.*;

/**
 * File
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/15 $
 **/

public class FileNd extends Node {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/pdl/nodes/FileNd.java#2 $ by $Author: dennis $, $DateTime: 2003/08/15 13:46:34 $";

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
