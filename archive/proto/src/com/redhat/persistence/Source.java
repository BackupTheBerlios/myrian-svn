package com.redhat.persistence;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.ObjectType;

/**
 * Source
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public class Source {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/Source.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

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
