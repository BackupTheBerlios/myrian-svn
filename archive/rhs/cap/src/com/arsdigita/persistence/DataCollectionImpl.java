/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.ObjectType;
import com.redhat.persistence.DataSet;
import com.redhat.persistence.common.Path;

/**
 * DataCollectionImpl
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/05/03 $
 **/

class DataCollectionImpl extends DataQueryImpl implements DataCollection {

    public final static String versionId = "$Id: //users/rhs/persistence/cap/src/com/arsdigita/persistence/DataCollectionImpl.java#1 $ by $Author: rhs $, $DateTime: 2004/05/03 11:00:53 $";

    DataCollectionImpl(Session ssn, DataSet ds) {
        super(ssn, ds);
    }

    public ObjectType getObjectType() {
        return C.fromType(getSession().getMetadataRoot(), getTypeInternal());
    }

    public DataObject getDataObject() {
        Path p = Path.get(null);
        p = resolvePath(p);
        return (DataObject) m_cursor.get(p);
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
