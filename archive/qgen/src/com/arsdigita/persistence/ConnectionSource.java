package com.arsdigita.persistence;

import java.sql.Connection;

/**
 * ConnectionSource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/10 $
 **/

public interface ConnectionSource {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/arsdigita/persistence/ConnectionSource.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

    Connection acquire();

    void release(Connection conn);

}
