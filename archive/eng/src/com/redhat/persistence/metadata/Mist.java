/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
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
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

class Mist extends AbstractList {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/metadata/Mist.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
