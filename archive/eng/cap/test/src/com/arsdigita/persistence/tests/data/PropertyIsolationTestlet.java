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
package com.arsdigita.persistence.tests.data;

import com.arsdigita.persistence.*;
import com.arsdigita.persistence.metadata.*;

import java.util.*;

import org.apache.log4j.Logger;

public class PropertyIsolationTestlet extends Testlet {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/tests/data/PropertyIsolationTestlet.java#1 $ by $Author: rhs $, $DateTime: 2004/06/07 13:49:55 $";

    private static final Logger s_log = Logger.getLogger(PropertyIsolationTestlet.class);

    private DataSource m_initial = new DataSource("initial values");
    private DataSource m_updated = new DataSource("updated values");
    private DataSource m_other = new DataSource("other values");

    private ObjectType m_type;
    private String[] m_path;

    public PropertyIsolationTestlet(String type, String[] path) {
        this(MetadataRoot.getMetadataRoot().getObjectType(type), path);
    }

    public PropertyIsolationTestlet(ObjectType type, String[] path) {
        m_type = type;
        m_path = path;
    }

    public void run() {
        ObjectTree tree = makeTree(m_type, ATTRIBUTE | ROLE, COLLECTION, 1);

        ObjectTree updated = new ObjectTree(m_type);
        updated.addPath(m_path);

        // Create
        DataObject data = create(tree, m_initial);
        data.save();

        // Create other
        DataObject other = create(tree, m_other);
        other.save();

        // Update
        update(data, updated, m_updated);
        data.save();
        update(other, updated, m_updated);
        other.save();

        // Retrieve and verify
        verify(data.getSession().retrieve(data.getOID()), updated, m_updated);
    }
}
