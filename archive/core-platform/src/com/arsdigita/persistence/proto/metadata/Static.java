package com.arsdigita.persistence.proto.metadata;

import com.arsdigita.persistence.proto.common.*;

/**
 * Static
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class Static extends Mapping {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/metadata/Static.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

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
