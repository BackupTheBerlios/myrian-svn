package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.ObjectType;

/**
 * Source
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class Source {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/Source.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    private ObjectType m_type;
    private Path m_path;

    public Source(ObjectType type) {
        this(type, null);
    }

    public Source(ObjectType type, Path path) {
        m_type = type;
        m_path = path;
    }

    public ObjectType getObjectType() {
        return m_type;
    }

    public Path getPath() {
        return m_path;
    }

}
