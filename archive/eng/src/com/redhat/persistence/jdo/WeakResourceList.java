package com.redhat.persistence.jdo;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

abstract class WeakResourceList {
    private List m_refs = new LinkedList();

    public void add(Object o) {
        purge();
        m_refs.add(new WeakReference(o));
    }

    public void purge() {
        remove(null);
    }

    public boolean remove(Object obj) {
        for (Iterator it = m_refs.iterator(); it.hasNext(); ) {
            WeakReference ref = (WeakReference) it.next();
            Object cur = ref.get();
            if (cur == null) {
                it.remove();
            } else if (cur == obj) {
                it.remove();
                return true;
            }
        }

        return false;
    }

    protected abstract void onRelease(Object o);

    public void release() {
        while (m_refs.size() > 0) {
            Object o = ((WeakReference) m_refs.remove(0)).get();
            if (o != null) {
                onRelease(o);
            }
        }
    }
}
