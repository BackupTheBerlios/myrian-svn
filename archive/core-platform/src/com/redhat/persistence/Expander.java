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

package com.redhat.persistence;

import com.arsdigita.util.UncheckedWrapperException;
import com.redhat.persistence.metadata.Adapter;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Property;
import com.redhat.persistence.metadata.Role;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Expands events. Each top level call to Session creates an exapnder and uses
 * it to expand the requested event. The expander prepares the events, but
 * does not activate any of them. If expansion fails, the session is not left
 * in an inconsistent state.
 */
class Expander extends Event.Switch {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/Expander.java#4 $ by $Author: jorris $, $DateTime: 2003/10/28 18:36:21 $";

    final private Session m_ssn;
    final private Collection m_deleting = new HashSet();
    final private List m_deletes = new LinkedList();
    // m_deleted indicates if any deletes were added to m_pending
    private boolean m_deleted = false;
    // m_pending == null indicates this expander is no longer usable
    private List m_pending = new ArrayList();

    Expander(Session ssn) {
        m_ssn = ssn;
    }

    private void addEvent(Event ev) {
        if (m_pending == null) {
            throw new IllegalStateException
                ("using expander after call to finish");
        }

        ev.prepare();
        m_pending.add(ev);
        ev.log();
        if (ev instanceof DeleteEvent) {
            m_deleted = true;
        }
    }

    private void addEvents(List l) {
        for (Iterator it = l.iterator(); it.hasNext(); ) {
            addEvent((Event) it.next());
        }
    }

    final void expand(Event ev) {
        try {
            Session.trace(ev.getName(), new Object[] { ev });
            ev.dispatch(this);
        } catch (RuntimeException re) {
            if (re instanceof ProtoException) {
                ProtoException pe = (ProtoException) re;
                if (pe.isInternal()) {
                    throw new UncheckedWrapperException
                        ("internal persistence exception", pe);
                } else {
                    pe.setInternal(true);
                }
            }

            throw re;
        } finally {
            Session.untrace(ev.getName());
        }
    }

    final List finish() {
        addEvents(m_deletes);
        List result = m_pending;
        // mark expander as unusable
        m_pending = null;
        return result;
    }

    final boolean didDelete() {
        return m_deleted;
    }

    public void onCreate(CreateEvent e) {
        final Object obj = e.getObject();

        ObjectData od = m_ssn.getObjectData(obj);
        if (od == null) {
            od = new ObjectData(m_ssn, obj, od.INFANTILE);
        } else if (!od.isDeleted()) {
            od.dump();
            ProtoException pe = new DuplicateObjectException(obj);
            pe.setInternal(false);
            throw pe;
        } else {
            od.setState(od.INFANTILE);
        }

        Adapter a = m_ssn.getAdapter(obj);
        a.setSession(obj, m_ssn);

        addEvent(e);

        PropertyMap props = a.getProperties(obj);
        for (Iterator it = props.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            Event ev;
            try {
                ev = new SetEvent
                    (m_ssn, obj, (Property) me.getKey(), me.getValue());
            } catch (TypeException te) {
                te.setInternal(false);
                throw te;
            }
            expand(ev);
        }
    }

    public void onDelete(DeleteEvent e) {
        final Object obj = e.getObject();

        if (m_ssn.isDeleted(obj) || isBeingDeleted(obj)) { return; }

        beginDelete(obj);

        ObjectType type = m_ssn.getObjectType(obj);

        for (Iterator it = type.getRoles().iterator(); it.hasNext(); ) {
            Role role = (Role) it.next();
            if (role.isCollection()) { clear(obj, role); }
        }

        for (Iterator it = type.getRoles().iterator(); it.hasNext(); ) {
            Role role = (Role) it.next();

            if (!role.isCollection() && m_ssn.get(obj, role) != null) {
                Event ev = new SetEvent(m_ssn, obj, role, null);
                expand(ev);
            }
        }

        // delete actually added at end
        m_deletes.add(e);
    }

    public void onSet(SetEvent e) {
        final Object obj = e.getObject();
        final Role role = (Role) e.getProperty();
        final Object value = e.getArgument();

        Object old = null;
        if (role.isComponent() || role.isReversable()) {
            old = m_ssn.get(obj, role);
        }

        if (role.isReversable()) {
            if (old != null) { reverseUpdateOld(e, old); }
            if (value != null) { reverseUpdateNew(e); }
        }

        addEvent(e);

        if (role.isComponent()) {
            if (old != null && !equals(old, value)) {
                cascadeDelete(obj, old);
            }
        }
    }

    public void onAdd(AddEvent e) {
        Role role = (Role) e.getProperty();
        if (role.isReversable()) { reverseUpdateNew(e); }
        addEvent(e);
    }

    public void onRemove(RemoveEvent e) {
        Role role = (Role) e.getProperty();

        if (role.isReversable()) { reverseUpdateOld(e, e.getArgument()); }

        addEvent(e);

        if (role.isComponent()) {
            cascadeDelete(e.getObject(), e.getArgument());
        }
    }

    // also called by session
    private void clear(Object obj, Property prop) {
        PersistentCollection pc =
            (PersistentCollection) m_ssn.get(obj, prop);
        Cursor c = pc.getDataSet().getCursor();
        while (c.next()) {
            expand(new RemoveEvent(m_ssn, obj, prop, c.get()));
        }
    }

    /**
     * Cascades delete from container to the containee. The container
     * can not be deleted by a cascading delete.
     **/
    private void cascadeDelete(Object container, Object containee) {
        boolean me = false;

        if (!isBeingDeleted(container)) {
            me = true;
            beginDelete(container);
        }

        expand(new DeleteEvent(m_ssn, containee));

        if (me) { undelete(container); }
    }

    /**
     * Update the reverse role of an old target object. After updating a
     * reversible role, either set or remove, the reverse role for the old
     * target needs to be set to null.
     *
     * @param event the event causing this update
     * @param target the old target of a role that has been updated
     **/
    private void reverseUpdateOld(PropertyEvent event, Object target) {
        Object source = event.getObject();
        Role role = (Role) event.getProperty();
        Role rev = role.getReverse();

        // if object is being or will be deleted, do nothing
        if (m_ssn.isDeleted(target)
            || isBeingDeleted(target)
            || role.isComponent()) {
            return;
        }

        if (rev.isCollection()) {
            addEvent(new RemoveEvent(m_ssn, target, rev, source, event));
        } else {
            addEvent(new SetEvent(m_ssn, target, rev, null, event));
        }
    }

    /**
     * Update the reverse role of a new target object. After updating a
     * reversible role, either set or add, the reverse role for the new target
     * needs to be set to the new source. In addition, the new target's old
     * source needs to be updated so its role target is null.
     **/
    private void reverseUpdateNew(PropertyEvent event) {
        Object source = event.getObject();
        Role role = (Role) event.getProperty();
        Object target = event.getArgument();
        Role rev = role.getReverse();
        if (rev.isCollection()) {
            addEvent(new AddEvent(m_ssn, target, rev, source, event));
        } else {
            Object old = m_ssn.get(target, rev);
            if (old != null) {
                if (role.isCollection()) {
                    addEvent(new RemoveEvent(m_ssn, old, role, target, event));
                } else {
                    addEvent(new SetEvent(m_ssn, old, role, null, event));
                }
            }

            addEvent(new SetEvent(m_ssn, target, rev, source, event));
        }
    }

    private void beginDelete(Object obj) {
        m_deleting.add(m_ssn.getSessionKey(obj));
    }

    private void undelete(Object obj) {
        m_deleting.remove(m_ssn.getSessionKey(obj));
    }

    private boolean isBeingDeleted(Object obj) {
        return m_deleting.contains(m_ssn.getSessionKey(obj));
    }

    private boolean equals(Object o1, Object o2) {
        if (o1 == null || o2 == null) {
            return o1 == o2;
        } else {
            return m_ssn.getSessionKey(o1).equals(m_ssn.getSessionKey(o2));
        }
    }

}
