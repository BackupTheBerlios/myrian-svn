/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
 *
 */
package com.arsdigita.util;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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


/**
 * Class DummyServletConfig
 *
 * @author jorris@redhat.com
 */
public class DummyServletConfig implements ServletConfig {
    private String m_servletName;
    private DummyServletContext m_ctx = new DummyServletContext();
    private Map m_initParameters = new HashMap();

    public DummyServletConfig(String servletName) {
        m_servletName = servletName;
    }

    public String getServletName() {
        return m_servletName;
    }

    public ServletContext getServletContext() {
        return m_ctx;
    }

    public String getInitParameter(String name) {
        String param = (String) m_initParameters.get(name);
        return param;
    }

    public Enumeration getInitParameterNames() {
        final Iterator iter = m_initParameters.keySet().iterator();
        Enumeration enum = new Enumeration() {
            public boolean hasMoreElements() {
                return iter.hasNext();
            }

            public Object nextElement() {
                return iter.next();
            }
        };

        return enum;
    }

    public void setInitParameter(String name, String value) {
        m_initParameters.put(name, value);
    }

}
