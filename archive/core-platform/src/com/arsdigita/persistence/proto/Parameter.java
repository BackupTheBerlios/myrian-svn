package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.ObjectType;

/**
 * Parameter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class Parameter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/Parameter.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

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

    public String toString() {
	return m_type + " " + m_path;
    }

}
