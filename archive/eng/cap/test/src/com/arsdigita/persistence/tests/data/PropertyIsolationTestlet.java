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
package com.arsdigita.persistence.tests.data;

import com.arsdigita.persistence.*;
import com.arsdigita.persistence.metadata.*;

import java.util.*;

import org.apache.log4j.Logger;

public class PropertyIsolationTestlet extends Testlet {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/tests/data/PropertyIsolationTestlet.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
