package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.metadata.*;
import com.arsdigita.util.StringUtils;

/**
 * Path
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/25 $
 **/

class Path {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/Path.java#1 $ by $Author: rhs $, $DateTime: 2002/11/25 19:30:13 $";

    private ObjectType m_root;
    private String[] m_parts;

    private Path(ObjectType root, String path) {
        m_root = root;
        m_parts = StringUtils.split(path, '.');
    }

    public static final Path getInstance(ObjectType root, String path) {
        return new Path(root, path);
    }

}
