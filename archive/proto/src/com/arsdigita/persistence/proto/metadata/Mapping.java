package com.arsdigita.persistence.proto.metadata;

import com.arsdigita.persistence.proto.common.*;

/**
 * Mapping
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/01/15 $
 **/

public abstract class Mapping extends Element {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/Mapping.java#3 $ by $Author: rhs $, $DateTime: 2003/01/15 16:58:00 $";

    private Path m_path;

    protected Mapping(Path path) {
        m_path = path;
    }

    public ObjectMap getObjectMap() {
        return (ObjectMap) getParent();
    }

    public Path getPath() {
        return m_path;
    }

    Object getKey() {
        return getPath();
    }

}
