/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectObserver;
import com.arsdigita.domain.ObservableDomainObject;

import org.apache.log4j.Logger;

/**
 * <p>
 * A class to allow a column with denormalization to be maintained.
 * This abstract class can be subclassed to allow an object to easily
 * maintain a denormalized hierarcy (such as the default parents for
 * a category).
 * </p>
 *
 * @author <a href="mailto:randyg@alum.mit.edu">Randy Graebner</a>
 * @version $Revision: #1 $ $Date: 2003/12/10 $
 */
public abstract class HierarchyDenormalization {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/arsdigita/util/HierarchyDenormalization.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

    private final static Logger s_log =
        Logger.getLogger(HierarchyDenormalization.class);

    public HierarchyDenormalization(String operationName,
                                    ObservableDomainObject object,
                                    String attributeName) {
        this(operationName, object, attributeName, "id");
    }

    // id must be a property in the OID
    public HierarchyDenormalization(String operationName,
                                    ObservableDomainObject object,
                                    String attributeName, String id) {
        object.addObserver(new Observer(operationName, attributeName, id));
    }

    private class Observer implements DomainObjectObserver {

        private final String m_attributeName;
        private final String m_id;
        private final String m_operationName;
        private boolean m_isModified = false;
        private String m_oldAttributeValue;
        private String m_newAttributeValue;

        Observer(String operationName, String attributeName, String id) {
            m_id = id;
            m_operationName = operationName;
            m_attributeName = attributeName;
        }

        public void set(DomainObject dobj, String name,
                        Object old_value, Object new_value) {
            if (name.equals(m_attributeName)) {
                if (!m_isModified) {
                    m_oldAttributeValue = (String) old_value;
                    m_newAttributeValue = (String) new_value;
                    m_isModified = true;
                } else {
                    m_newAttributeValue = (String) new_value;
                }
            }
        }

        public void add(DomainObject dobj, String name,
                        DataObject dataObject) { }

        public void remove(DomainObject dobj, String name,
                           DataObject dataObject) { }

        public void clear(DomainObject dobj, String name) { }

        public void beforeSave(DomainObject dobj) { }

        public void afterSave(DomainObject dobj) {
            if (m_isModified) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("After save: oid:" + dobj.getOID() +
                                " new value is:" + m_newAttributeValue +
                                " old value is:" + m_oldAttributeValue);
                }

                if ((m_oldAttributeValue == null
                     && m_newAttributeValue == null)
                    || (m_oldAttributeValue != null
                        && m_oldAttributeValue.equals(m_newAttributeValue))) {
                    return;
                }

                if (m_oldAttributeValue == null) {
                    // after save triggered by autoflush in before save
                    m_isModified = false;
                    return;
                }

                DataOperation operation =
                    SessionManager.getSession().retrieveDataOperation
                    (m_operationName);
                operation.setParameter("id", dobj.getOID().get(m_id));
                operation.setParameter("newPrefix", m_newAttributeValue);
                operation.setParameter
                    ("oldPrefixLength", new Integer
                     (m_oldAttributeValue.length()));
                operation.setParameter("oldPrefix", m_oldAttributeValue);
                operation.execute();
            }
            m_isModified = false;
        }

        public void beforeDelete(DomainObject dobj) { }
        public void afterDelete(DomainObject dobj) { }

        public boolean equals(Object other) {
            if (other instanceof Observer) {
                Observer o = (Observer) other;
                return m_operationName.equals(o.m_operationName)
                    && m_id.equals(o.m_id)
                    && m_attributeName.equals(o.m_attributeName);
            }

            return false;
        }

        public int hashCode() {
            return m_operationName.hashCode();
        }
    }

    /**
     * @deprecated HierarchyDenormalization now uses a DomainObjectObserver
     */
    public void beforeSave() { }

    /**
     * @deprecated HierarchyDenormalization now uses a DomainObjectObserver
     */
    public void afterSave() { }
}
