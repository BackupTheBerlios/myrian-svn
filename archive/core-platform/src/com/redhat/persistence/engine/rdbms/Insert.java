package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;

import java.util.*;

/**
 * Insert
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

class Insert extends DML {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/engine/rdbms/Insert.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    public Insert(Table table) {
        super(table);
    }

    void write(SQLWriter w) {
        w.write(this);
    }

}
