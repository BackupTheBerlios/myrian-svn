/*
 * Copyright (C) 2001-2004 Red Hat, Inc.  All Rights Reserved.
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
package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.ObjectType;
import com.redhat.persistence.DataSet;
import com.redhat.persistence.common.Path;

/**
 * DataCollectionImpl
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/08/30 $
 **/

class DataCollectionImpl extends DataQueryImpl implements DataCollection {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/DataCollectionImpl.java#3 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    DataCollectionImpl(Session ssn, DataSet ds) {
        super(ssn, ds);
    }

    public ObjectType getObjectType() {
        return C.fromType(getSession().getMetadataRoot(), getTypeInternal());
    }

    public DataObject getDataObject() {
        Path p = Path.get(null);
        p = resolvePath(p);
        return (DataObject) getSession().refresh(m_cursor.get(p));
    }

    /**
     * @deprecated
     **/

    public void setParameter(String p, Object o) {
        super.setParameter(p, o);
    }

    /**
     * @deprecated
     **/

    public Object getParameter(String p) {
        return super.getParameter(p);
    }

    public boolean contains(OID oid) {
        throw new Error("not implemented");
    }

    public boolean contains(DataObject data) {
        throw new Error("not implemented");
    }

}
