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
package org.myrian.persistence.jdo;

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
