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
package com.redhat.persistence;

import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Property;
import com.redhat.persistence.oql.Query;

/**
 * QuerySource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class QuerySource {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/QuerySource.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private Session m_ssn = null;

    void setSession(Session ssn) {
        m_ssn = ssn;
    }

    public Session getSession() {
        return m_ssn;
    }

    public Query getQuery(ObjectType type) {
        throw new UnsupportedOperationException();
    }

    public Query getQuery(PropertyMap keys) {
        throw new UnsupportedOperationException();
    }

    // These should probably be changed to take type signatures.
    public Query getQuery(Object obj) {
        throw new UnsupportedOperationException();
    }

    public Query getQuery(Object obj, Property prop) {
        throw new UnsupportedOperationException();
    }

}
