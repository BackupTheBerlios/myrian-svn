/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.persistence.tests.data;

import com.arsdigita.persistence.*;
import com.arsdigita.persistence.metadata.*;

import java.util.*;

import org.apache.log4j.Logger;

public class DoubleUpdateTestlet extends Testlet {

    public final static String versionId = "$Id: //core-platform/proto/test/src/com/arsdigita/persistence/tests/data/DoubleUpdateTestlet.java#4 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    private static final Logger s_log = Logger.getLogger(DoubleUpdateTestlet.class);

    private DataSource m_initial = new DataSource("initial values");
    private DataSource m_updated = new DataSource("updated values");

    private ObjectType m_type;
    private String[] m_path;

    public DoubleUpdateTestlet(String type, String[] path) {
        this(MetadataRoot.getMetadataRoot().getObjectType(type), path);
    }

    public DoubleUpdateTestlet(ObjectType type, String[] path) {
        m_type = type;
        m_path = path;
    }

    public void run() {
        ObjectTree tree = makeTree(m_type, ATTRIBUTE | ROLE, COLLECTION, 1);
        ObjectTree updated = new ObjectTree(m_type);
        updated.addPath(m_path);
        addPaths(updated.getSubtree(m_path[0]), ATTRIBUTE | ROLE, COLLECTION,
                 0);

        // Create
        DataObject data = create(tree, m_initial);
        data.save();

        // Update
        update(data, updated, m_updated);
        data.save();
        update(data, updated, m_updated);
        data.save();

        // Retrieve and verify
        verify(data.getSession().retrieve(data.getOID()), updated, m_updated);
    }
}
