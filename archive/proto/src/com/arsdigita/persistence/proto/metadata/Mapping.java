package com.arsdigita.persistence.proto.metadata;

/**
 * Mapping
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/01/15 $
 **/

public abstract class Mapping extends Element {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/Mapping.java#2 $ by $Author: rhs $, $DateTime: 2003/01/15 09:35:55 $";

    private Path m_path;

    protected Mapping(Path path) {
        m_path = path;
    }

    public ObjectMap getObjectMap() {
        return (ObjectMap) getParent();
    }

    public Path getPath() {
        return m_path;
    }

    Object getKey() {
        return getPath();
    }

}
