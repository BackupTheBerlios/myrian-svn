package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.metadata.*;
import java.util.*;

/**
 * Signature
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/25 $
 **/

public class Signature {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/Signature.java#1 $ by $Author: rhs $, $DateTime: 2002/11/25 19:30:13 $";

    private ObjectType m_type;
    private HashMap m_paths = new HashMap();

    public Signature(ObjectType type) {
        m_type = type;
    }

    public void addPath(String path) {
        if (!m_paths.containsKey(path)) {
            m_paths.put(path, Path.getInstance(m_type, path));
        }
    }

    Path getPath(String path) {
        return (Path) m_paths.get(path);
    }

}
