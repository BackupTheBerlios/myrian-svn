package com.arsdigita.persistence;

import com.arsdigita.util.jdbc.*;
import java.sql.*;

/**
 * DedicatedConnectionSource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/10 $
 **/

public class DedicatedConnectionSource implements ConnectionSource {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/arsdigita/persistence/DedicatedConnectionSource.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

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
