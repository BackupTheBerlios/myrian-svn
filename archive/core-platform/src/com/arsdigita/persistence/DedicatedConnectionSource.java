package com.arsdigita.persistence;

import com.arsdigita.util.jdbc.*;
import java.sql.*;

/**
 * DedicatedConnectionSource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/11/07 $
 **/

public class DedicatedConnectionSource implements ConnectionSource {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/DedicatedConnectionSource.java#2 $ by $Author: rhs $, $DateTime: 2003/11/07 19:15:58 $";

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
