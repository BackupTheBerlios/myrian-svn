package com.redhat.persistence;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.ObjectType;

/**
 * Parameter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public class Parameter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/Parameter.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

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
