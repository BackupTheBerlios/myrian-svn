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

package com.arsdigita.util;

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.domain.DomainObject;

/**
 * <p>
 * A class to allow a column with denormazliation to be maintained.
 * This abstract class can be subclassed to allow an object to easily
 * maintain a denormalized hierarcy (such as the default parents for
 * a category).  
 * </p>
 *
 * @author <a href="mailto:randyg@alum.mit.edu">Randy Graebner</a>
 * @version $Revision: #1 $ $Date: 2002/08/28 $
 */
public abstract class HierarchyDenormalization {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/util/HierarchyDenormalization.java#1 $ by $Author: randyg $, $DateTime: 2002/08/28 17:43:50 $";

    private String m_attributeName;
    private String m_id;
    private DomainObject m_domainObject;
    private String m_operationName;
    private boolean m_isModified = false;
    private String m_oldAttributeValue;


    public HierarchyDenormalization(String operationName, DomainObject object,
                                    String attributeName) {
        this(operationName, object, attributeName, "id");
    }

    // id must be a property in the OID
    public HierarchyDenormalization(String operationName, DomainObject object, 
                                    String attributeName, String id) {
        m_id = id;
        m_operationName = operationName;
        m_domainObject = object;
        m_attributeName = attributeName;
    }


    /**
     *  This should be called by a domain object before calling super.save()
     *  so that the correct variables can be set in preparation for the
     *  {@link afterSave()} call.
     */
    public void beforeSave() {
        m_oldAttributeValue = null;
        m_isModified = m_domainObject.isPropertyModified(m_attributeName);
        boolean wasNew = m_domainObject.isNew();

        // if the url has been modified, we need the old url
        // if it is modified and new then this is the first url so
        // the "old" url is the "new" url
        if (m_isModified) {
            if (wasNew) {
                m_oldAttributeValue = getAttributeValue();
            } else {
                DataCollection collection =
                    SessionManager.getSession().retrieve
                    (m_domainObject.getObjectType().getQualifiedName());
                collection.addEqualsFilter(m_id, 
                                           m_domainObject.getOID().get(m_id));
                if (collection.next()) {
                    m_oldAttributeValue = (String)collection.get
                        (m_attributeName);
                }
                collection.close();
            }
        }
    }


    /**
     *  This updates the hierarchy and maintains the hierarchy for all
     *  of the other objects that have this object as a parent
     */
    public void afterSave() {
        if (m_isModified) {
            DataOperation operation =
                SessionManager.getSession().retrieveDataOperation
                (m_operationName);
            operation.setParameter("id", m_domainObject.getOID().get(m_id));
            operation.setParameter("newPrefix", getAttributeValue());
            operation.setParameter("oldPrefixLength", 
                                   new Integer(m_oldAttributeValue.length()));
            operation.setParameter("oldPrefix", m_oldAttributeValue);
            operation.execute();
        }
        m_isModified = false;
    }


    /**
     *  This returns the value of the actual attribute
     */
    public abstract String getAttributeValue();
}

