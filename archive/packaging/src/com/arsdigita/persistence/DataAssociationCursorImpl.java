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

package com.arsdigita.persistence;

import com.redhat.persistence.common.Path;
import com.arsdigita.persistence.metadata.Property;
import com.redhat.persistence.PersistentCollection;
import com.redhat.persistence.Signature;

/**
 * DataAssociationCursorImpl
 *
 * @author Archit Shah &lt;ashah@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/19 $
 **/

class DataAssociationCursorImpl extends DataCollectionImpl
    implements DataAssociationCursor {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/persistence/DataAssociationCursorImpl.java#2 $ by $Author: rhs $, $DateTime: 2003/08/19 22:28:24 $";

    private DataAssociationImpl m_assn;

    DataAssociationCursorImpl(Session ssn, DataObject data, Property prop) {
        super(ssn, (PersistentCollection) ssn.getProtoSession().get(data, C.prop(prop)));
    }

    protected final void setAssociation(DataAssociationImpl assn) {
        m_assn = assn;
    }

    public DataAssociation getDataAssociation() {
        return m_assn;
    }

    public DataObject getLink() {
        Signature sig = getOriginal().getSignature();
        if (sig.isSource(Path.get("link"))) {
            return (DataObject) get("link");
        } else {
            return null;
        }
    }


    /**
     *  This returns the Link Property specified by the passed in parameter.
     *  For instance, if there is a sortKey specifying how to sort
     *  the association, calling getLinkProperty("sortKey") would return
     *  the sortKey for the given Association.
     *
     *  @param name The name of the Link Property to return
     *  @return The Link Property specified by the parameter
     */
    public Object getLinkProperty(String name) {
        return get("link." + name);
    }

    public void remove() {
        m_assn.remove(getDataObject());
    }
}
