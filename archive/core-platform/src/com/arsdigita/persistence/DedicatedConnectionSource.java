package com.arsdigita.persistence;

import java.sql.Connection;

/**
 * DedicatedConnectionSource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/10/23 $
 **/

public class DedicatedConnectionSource implements ConnectionSource {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/DedicatedConnectionSource.java#1 $ by $Author: justin $, $DateTime: 2003/10/23 15:28:18 $";

    private Connection m_conn;

    public DedicatedConnectionSource(Connection conn) {
        m_conn = conn;
    }

    public Connection acquire() {
        return m_conn;
    }

    public void release(Connection conn) {
        // do nothing
    }

}
