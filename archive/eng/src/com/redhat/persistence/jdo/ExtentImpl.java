/*
 * Copyright (C) 2004 Red Hat, Inc. All Rights Reserved.
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
package com.redhat.persistence.jdo;

import com.redhat.persistence.*;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.oql.Expression;
import java.util.Iterator;
import javax.jdo.Extent;
import javax.jdo.PersistenceManager;

class ExtentImpl extends CRPCollection implements Extent {

    private final PersistenceManagerImpl m_pmi;
    private final Class m_cls;
    private final boolean m_subclasses;
    private final ObjectType m_type;
    private final Expression m_expr;
    private final Signature m_sig;

    ExtentImpl(PersistenceManagerImpl pmi, Class cls, boolean subclasses) {
        m_pmi = pmi;
        m_cls = cls;
        m_subclasses = subclasses;

        DataSet ds = C.all(pmi.getSession(), cls).getDataSet();
        m_sig = ds.getSignature();
        m_type = ds.getSignature().getObjectType();
        m_expr = ds.getExpression();

        if (!subclasses) {
            // XXX checking if there is a subtype
            for (Iterator it = ssn().getRoot().getObjectTypes().iterator();
                 it.hasNext(); ) {
                if (m_type.equals(((ObjectType) it.next()).getSupertype())) {
                    throw new Error("not implemented");
                }
            }
        }
    }

    public PersistenceManager getPersistenceManager() {
        return m_pmi;
    }

    public Class getCandidateClass() {
        return m_cls;
    }

    public boolean hasSubclasses() {
        return m_subclasses;
    }

    protected Signature signature() {
        return m_sig;
    }

    public Expression expression() {
        return m_expr;
    }

    Session ssn() {
        return m_pmi.getSession();
    }

    ObjectType type() {
        return m_type;
    }

    public void close(Iterator it) {
        // XXX check to make sure it is one of ours
        Extensions.close(it);
    }

    public void closeAll() {
        super.close();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean add(Object o) {
        throw new UnsupportedOperationException();
    }

    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }
}
