package com.arsdigita.persistence.proto.metadata;

import com.arsdigita.persistence.proto.common.*;

/**
 * Mapping
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2003/01/28 $
 **/

public abstract class Mapping extends Element {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/Mapping.java#5 $ by $Author: rhs $, $DateTime: 2003/01/28 19:17:39 $";

    public static abstract class Switch {
        public abstract void onValue(ValueMapping vm);
        public abstract void onReference(ReferenceMapping vm);
    }

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

    public abstract void dispatch(Switch sw);

    public abstract boolean isValue();

    public abstract boolean isReference();

    Object getKey() {
        return getPath();
    }

    public String toString() {
        return "<mapping for " + m_path + ">";
    }

}
