/*
 * Copyright (C) 2001-2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.Property;
import org.myrian.persistence.ProtoException;

/**
 * DataAssociationImpl
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/10/01 $
 **/

class DataAssociationImpl extends DataAssociationCursorImpl
    implements DataAssociation {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/DataAssociationImpl.java#5 $ by $Author: vadim $, $DateTime: 2004/10/01 18:41:18 $";

    private org.myrian.persistence.Session m_pssn;
    private DataObjectImpl m_data;
    private Property m_prop;
    private org.myrian.persistence.metadata.Property m_pprop;

    DataAssociationImpl(Session ssn, DataObjectImpl data, Property prop) {
        super(ssn, data, prop);
        setAssociation(this);
        m_pssn = ssn.getProtoSession();
        m_data = data;
        m_prop = prop;
        m_pprop = C.prop(m_pssn.getRoot(), prop);
    }

    public DataObject add(DataObject obj) {
        m_data.validateWrite();
        try {
            return (DataObject) m_pssn.add(m_data, m_pprop, obj);
        } catch (ProtoException pe) {
            throw PersistenceException.newInstance(pe);
        }
    }

    org.myrian.persistence.metadata.Property getProperty() {
        return m_pprop;
    }

    public void clear() {
        m_data.validateWrite();
        m_pssn.clear(m_data, m_pprop);
    }

    public DataCollection getDataCollection() { return cursor(); }

    public DataAssociationCursor getDataAssociationCursor() {
        DataAssociationCursorImpl dac = new DataAssociationCursorImpl(
            getSession(), m_data, m_prop);
        dac.setAssociation(this);
        return dac;
    }

    public DataAssociationCursor cursor() {
        return getDataAssociationCursor();
    }

    public void remove(DataObject obj) {
        m_data.validateWrite();
        try {
            m_pssn.remove(m_data, m_pprop, obj);
        } catch (ProtoException pe) {
            throw PersistenceException.newInstance(pe);
        }
    }

    public void remove(OID oid) {
        m_data.validateWrite();
        remove(getSession().retrieve(oid));
    }

    public boolean isModified() {
        return !m_pssn.isFlushed(m_data, m_pprop);
    }
}
