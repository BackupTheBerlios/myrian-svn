package com.arsdigita.persistence.proto.metadata;

/**
 * Mapping
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/31 $
 **/

public abstract class Mapping {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/Mapping.java#1 $ by $Author: rhs $, $DateTime: 2002/12/31 15:39:17 $";

    private ObjectMap m_map = null;
    private Path m_path;

    protected Mapping(Path path) {
        m_path = path;
    }

    public ObjectMap getObjectMap() {
        return m_map;
    }

    void setObjectMap(ObjectMap map) {
        m_map = map;
    }

    public Path getPath() {
        return m_path;
    }

}
