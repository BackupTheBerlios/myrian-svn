package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.*;
import com.arsdigita.util.UncheckedWrapperException;
import java.util.*;

/**
 * Expands events. Each top level call to Session creates an exapnder and uses
 * it to expand the requested event. The expander prepares the events, but
 * does not activate any of them. If expansion fails, the session is not left
 * in an inconsistent state.
 */
class Expander extends Event.Switch {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Expander.java#1 $ by $Author: ashah $, $DateTime: 2003/04/01 18:05:32 $";

    final private Session m_ssn;
    final private Set m_visiting = new HashSet();
    final private List m_pending;

    Expander(Session ssn, List pending) {
        m_ssn = ssn;
        m_pending = pending;
    }

    private void addEvent(Event ev) {
        ev.prepare();
        m_pending.add(ev);
        ev.log();
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

    public void onCreate(CreateEvent e) {
        final Object obj = e.getObject();

        ObjectData od = m_ssn.getObjectData(obj);
        if (od == null) {
            od = new ObjectData(m_ssn, obj, od.INFANTILE);
        } else if (!od.isDeleted()) {
            od.dump();
            throw new IllegalArgumentException
                ("Object already exists: " + obj);
        } else {
            od.setState(od.INFANTILE);
        }

        Adapter a = m_ssn.getAdapter(obj);
        a.setSession(obj, m_ssn);

        addEvent(e);

        PropertyMap props = a.getProperties(obj);
        for (Iterator it = props.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            Event ev = new SetEvent
                (m_ssn, obj, (Property) me.getKey(), me.getValue());
            expand(ev);
        }
    }

    public void onDelete(DeleteEvent e) {
        final Object obj = e.getObject();
        ObjectData od = m_ssn.fetchObjectData(obj);

        if (od == null || m_visiting.contains(od)) {
            return;
        }

        m_visiting.add(od);

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

        m_visiting.remove(od);

        addEvent(e);
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
            if (old != null) { cascadeDelete(obj, old); }
        }
    }

    public void onAdd(AddEvent e) {
        Role role = (Role) e.getProperty();
        if (role.isReversable()) { reverseUpdateNew(e); }
        addEvent(e);
    }

    public void onRemove(RemoveEvent e) {
        addEvent(e);

        Role role = (Role) e.getProperty();

        if (role.isReversable()) {
            reverseUpdateOld(e, e.getArgument());
        }

        if (role.isComponent()) {
            cascadeDelete(e.getObject(), e.getArgument());
        }
    }

    // also called by session
    void clear(Object obj, Property prop) {
        PersistentCollection pc =
            (PersistentCollection) m_ssn.get(obj, prop);
        Cursor c = pc.getDataSet().getCursor();
        while (c.next()) {
            expand(new RemoveEvent(m_ssn, obj, prop, c.get()));
        }
    }

    /**
     * Cascades delete from container to the containee. The containee
     * will not be deleted in all cases.
     **/
    private void cascadeDelete(Object container, Object containee) {
        ObjectData containerOD = m_ssn.getObjectData(container);
        if (containerOD == null) {
            throw new IllegalStateException("container object not present");
        }
        boolean me = false;

        if (!m_visiting.contains(containerOD)) {
            me = true;
            m_visiting.add(containerOD);
        }

        expand(new DeleteEvent(m_ssn, containee));

        if (me) { m_visiting.remove(containerOD); }
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
}
