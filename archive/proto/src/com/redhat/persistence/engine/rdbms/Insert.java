package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;

import java.util.*;

/**
 * Insert
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

class Insert extends DML {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/engine/rdbms/Insert.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    public Insert(Table table) {
        super(table);
    }

    void write(SQLWriter w) {
        w.write(this);
    }

}
