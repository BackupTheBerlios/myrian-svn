package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.ObjectType;

/**
 * Parameter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/01/06 $
 **/

public class Parameter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Parameter.java#1 $ by $Author: rhs $, $DateTime: 2003/01/06 16:31:02 $";

    private ObjectType m_type;
    private Path m_path;

    public Parameter(ObjectType type, Path path) {
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
