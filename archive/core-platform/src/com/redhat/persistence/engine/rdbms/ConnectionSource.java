package com.redhat.persistence.engine.rdbms;

import java.sql.Connection;

/**
 * ConnectionSource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public interface ConnectionSource {

    Connection acquire();

    void release(Connection conn);

}