/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.arsdigita.persistence;

import com.arsdigita.util.jdbc.*;
import java.sql.*;

/**
 * DedicatedConnectionSource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/04/07 $
 **/

public class DedicatedConnectionSource implements ConnectionSource {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/DedicatedConnectionSource.java#4 $ by $Author: dennis $, $DateTime: 2004/04/07 16:07:11 $";

    private String m_url;
    private ThreadLocal m_connections = new ThreadLocal() {
        public Object initialValue() {
            return Connections.acquire(m_url);
        }
    };

    public DedicatedConnectionSource(String url) {
        m_url = url;
    }

    public Connection acquire() {
        return (Connection) m_connections.get();
    }

    public void release(Connection conn) {
        // do nothing
    }

}
