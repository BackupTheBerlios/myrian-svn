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

import com.arsdigita.persistence.metadata.Property;
import com.redhat.persistence.Signature;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.Link;
import com.redhat.persistence.metadata.ObjectType;

/**
 * DataAssociationCursorImpl
 *
 * @author Archit Shah &lt;ashah@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/05/03 $
 **/

class DataAssociationCursorImpl extends DataCollectionImpl
    implements DataAssociationCursor {

    public final static String versionId = "$Id: //users/rhs/persistence/cap/src/com/arsdigita/persistence/DataAssociationCursorImpl.java#1 $ by $Author: rhs $, $DateTime: 2004/05/03 11:00:53 $";

    private DataAssociationImpl m_assn;

    DataAssociationCursorImpl(Session ssn, DataObject data, Property prop) {
        super(ssn, ssn.getProtoSession().getDataSet
              (data, C.prop(ssn.getRoot(), prop)));
    }

    protected final void setAssociation(DataAssociationImpl assn) {
        m_assn = assn;
    }

    public DataAssociation getDataAssociation() {
        return m_assn;
    }

    private boolean hasLinkAttributes() {
        return (m_assn.getProperty() instanceof Link);
    }

    ObjectType getTypeInternal() {
        if (m_originalSig.isSource(Path.get("link"))) {
            return m_originalSig.getSource
                (Path.get(com.redhat.persistence.Session.LINK_ASSOCIATION))
                .getObjectType();
        } else {
            return super.getTypeInternal();
        }
    }

    boolean hasProperty(Path p) {
        boolean superAnswer = super.hasProperty(p);

        if (superAnswer || !hasLinkAttributes()) {
            return superAnswer;
        }

        return m_originalSig.exists(p);
    }

    protected Path resolvePath(Path path) {
        if (!hasLinkAttributes()) {
            return super.resolvePath(path);
        }

        if (path == null || getTypeInternal().getProperty(path) != null) {
            path = Path.add
                (com.redhat.persistence.Session.LINK_ASSOCIATION, path);
        }

        return super.resolvePath(path);
    }

    public DataObject getLink() {
        if (!hasLinkAttributes()) {
            return null;
        }

        return (DataObject) get("link");
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
        if (!hasLinkAttributes()) {
            return null;
        }

        return get("link." + name);
    }

    public void remove() {
        m_assn.remove(getDataObject());
    }
}
