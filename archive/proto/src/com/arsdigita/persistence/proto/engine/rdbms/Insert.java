package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;

/**
 * Insert
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2003/02/13 $
 **/

class Insert extends DML {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Insert.java#5 $ by $Author: rhs $, $DateTime: 2003/02/13 18:36:15 $";

    public Insert(Table table) {
        super(table);
    }

    public String toString() {
        StringBuffer cols = new StringBuffer();
        StringBuffer vals = new StringBuffer();
        for (Iterator it = getColumns().iterator(); it.hasNext(); ) {
            Column col = (Column) it.next();
            cols.append(col.getName());
            vals.append(get(col));

            if (it.hasNext()) {
                cols.append(", ");
                vals.append(", ");
            }
        }
        return "insert into " + getTable() +
            "\n(" + cols + ")\nvalues\n(" + vals + ")" + "\n\nparams = " +
            getParams();
    }

}
