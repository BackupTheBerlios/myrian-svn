package com.redhat.persistence.metadata;

import com.redhat.persistence.common.*;

/**
 * Static
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public class Static extends Mapping {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/metadata/Static.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    public Static(Path path) {
        super(path);
    }

    public Table getTable() {
        return null;
    }

    public void dispatch(Switch sw) {
        sw.onStatic(this);
    }

}
