package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.ObjectType;

/**
 * Parameter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/04/07 $
 **/

public class Parameter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Parameter.java#3 $ by $Author: rhs $, $DateTime: 2003/04/07 12:12:49 $";

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
	return m_path + ": " + m_type;
    }

}
