/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.redhat.persistence.metadata;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Mist
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/04/05 $
 **/

class Mist extends AbstractList {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/metadata/Mist.java#2 $ by $Author: rhs $, $DateTime: 2004/04/05 15:33:44 $";

    private Object m_parent = null;
    private ArrayList m_children = new ArrayList();
    private HashMap m_childrenMap = new HashMap();

    public Mist(Object parent) {
        m_parent = parent;
    }

    private Object check(Object o) {
        if (o == null) {
            throw new IllegalArgumentException
                ("null child");
        }

        if (!(o instanceof Element)) {
            throw new IllegalArgumentException
                ("not an element");
        }

        Element child = (Element) o;
        Object key = child.getElementKey();

        if (key == null) {
            throw new IllegalArgumentException
                ("null key");
        }

	return key;
    }

    public void add(int index, Object o) {
	Object key = check(o);
	Element child = (Element) o;

        if (child.getParent() != null) {
            throw new IllegalArgumentException
                ("child is already contained");
        }

        if (m_childrenMap.containsKey(key)) {
            throw new IllegalArgumentException
                ("duplicate key: " + key);
        }

        m_children.add(index, child);
        m_childrenMap.put(key, child);
        child.setParent(m_parent);
    }

    public Object get(int index) {
        return m_children.get(index);
    }

    public int size() {
        return m_children.size();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean remove(Object o) {
	Object key = check(o);
	Element child = (Element) o;
	if (!this.equals(child.getParent())) {
	    throw new IllegalArgumentException
		("child does not belong to this parent");
	}
        m_children.remove(o);
	m_childrenMap.remove(key);
	child.setParent(null);
	return true;
    }

    public Object[] toArray() {
        return m_children.toArray();
    }

    public Object[] toArray(Object[] a) {
        return m_children.toArray(a);
    }

    public Object get(Object key) {
        return m_childrenMap.get(key);
    }

    public boolean containsKey(Object key) {
        return m_childrenMap.containsKey(key);
    }

    public String toString() {
        return m_children.toString();
    }

}
