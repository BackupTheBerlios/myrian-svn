package com.arsdigita.persistence;

import java.sql.Connection;

/**
 * ConnectionSource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/10/23 $
 **/

public interface ConnectionSource {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/ConnectionSource.java#1 $ by $Author: justin $, $DateTime: 2003/10/23 15:28:18 $";

    Connection acquire();

    void release(Connection conn);

}
