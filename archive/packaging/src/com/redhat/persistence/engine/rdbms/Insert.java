package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;

import java.util.*;

/**
 * Insert
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 **/

class Insert extends DML {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/engine/rdbms/Insert.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

    public Insert(Table table) {
        super(table);
    }

    void write(SQLWriter w) {
        w.write(this);
    }

}
