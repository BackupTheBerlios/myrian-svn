package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.metadata.*;
import java.util.*;

/**
 * Signature
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/12/04 $
 **/

public class Signature {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Signature.java#2 $ by $Author: rhs $, $DateTime: 2002/12/04 19:18:22 $";

    private ObjectType m_type;
    private HashMap m_paths = new HashMap();

    public Signature(ObjectType type) {
        m_type = type;
    }

    public ObjectType getObjectType() {
        return m_type;
    }

    public void addPath(String path) {
        if (!m_paths.containsKey(path)) {
            m_paths.put(path, Path.getInstance(path));
        }
    }

    Path getPath(String path) {
        return (Path) m_paths.get(path);
    }

    public Collection getPaths() {
        return m_paths.values();
    }

    public String toString() {
        return getPaths().toString();
    }

}
