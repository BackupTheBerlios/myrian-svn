/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.util;

import javax.servlet.http.HttpSessionContext;
import javax.servlet.ServletContext;
import java.util.Hashtable;
import java.util.Enumeration;

public class HttpDummySession implements javax.servlet.http.HttpSession {

    Hashtable m_map = new Hashtable();
    long m_creationTime = System.currentTimeMillis();
    int m_maxInactive = -1;

    public  java.lang.Object getAttribute(java.lang.String name) {
        return m_map.get(name);
    }

    public java.util.Enumeration getAttributeNames() {
        return m_map.keys();
    }

    public long getCreationTime() {
        return m_creationTime;
    }

    public java.lang.String getId() {
        return "dummy_session";
    }

    public long getLastAccessedTime() {
        return System.currentTimeMillis();
    }

    public int getMaxInactiveInterval() {
        return m_maxInactive;
    }

    public HttpSessionContext getSessionContext() {
        return null;
    }

    public ServletContext getServletContext() {
        return null;
    }

    public java.lang.Object getValue(java.lang.String name) {
        return getAttribute(name);
    }

    public java.lang.String[] getValueNames() {
        throw new UnsupportedOperationException();
    }

    public void invalidate() {
        m_map.clear();
    }

    public boolean isNew() {
        return false;
    }

    public void putValue(java.lang.String name, java.lang.Object value) {
        setAttribute(name, value);
    }

    public void removeAttribute(java.lang.String name) {
        m_map.remove(name);
    }

    public  void removeValue(java.lang.String name) {
        removeAttribute(name);
    }

    public void setAttribute(java.lang.String name, java.lang.Object value) {
        m_map.put(name, value);
    }

    public void setMaxInactiveInterval(int interval) {
        m_maxInactive = interval;
    }
}
