package com.arsdigita.persistence.proto.metadata;

import com.arsdigita.persistence.proto.common.*;

/**
 * StaticMapping
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/02/26 $
 **/

public class StaticMapping extends Mapping {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/StaticMapping.java#1 $ by $Author: rhs $, $DateTime: 2003/02/26 12:01:31 $";

    public StaticMapping(Path path) {
        super(path);
    }

    public boolean isReference() {
        return false;
    }

    public boolean isValue() {
        return false;
    }

    public void dispatch(Switch sw) {
        sw.onStatic(this);
    }

}
