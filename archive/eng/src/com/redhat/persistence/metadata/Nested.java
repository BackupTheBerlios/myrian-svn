package com.redhat.persistence.metadata;

import com.redhat.persistence.common.*;

import java.util.*;

/**
 * Nested
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/08/05 $
 **/

public class Nested extends Mapping {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/metadata/Nested.java#1 $ by $Author: rhs $, $DateTime: 2004/08/05 12:04:47 $";

    public Nested(Path path, ObjectMap map) {
        super(path, map);
    }

    public List getColumns() {
        return getObjectMap().getColumns();
    }

    public Table getTable() {
        return null;
    }

    public void dispatch(Switch sw) {
        sw.onNested(this);
    }

}
