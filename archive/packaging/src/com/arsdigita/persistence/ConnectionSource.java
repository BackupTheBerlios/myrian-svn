package com.arsdigita.persistence;

import java.sql.Connection;

/**
 * ConnectionSource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/27 $
 **/

public interface ConnectionSource {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/persistence/ConnectionSource.java#1 $ by $Author: rhs $, $DateTime: 2003/08/27 19:33:58 $";

    Connection acquire();

    void release(Connection conn);

}
