package com.arsdigita.persistence;

import java.sql.Connection;

/**
 * DedicatedConnectionSource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/27 $
 **/

public class DedicatedConnectionSource implements ConnectionSource {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/persistence/DedicatedConnectionSource.java#1 $ by $Author: rhs $, $DateTime: 2003/08/27 19:33:58 $";

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
