package com.arsdigita.persistence.proto.metadata;

import com.arsdigita.persistence.proto.common.*;

/**
 * Link
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/01/15 $
 **/

public class Link extends Property {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/Link.java#3 $ by $Author: rhs $, $DateTime: 2003/01/15 16:58:00 $";

    private Path m_path;

    public Link(String name, Path path) {
        super(name);
        m_path = path;
    }

    public ObjectType getType() {
        throw new Error("Not implemented");
    }

    public boolean isCollection() {
        throw new Error("Not implemented");
    }

    public boolean isComponent() {
        throw new Error("Not implemented");
    }

    public void dispatch(Switch sw) {
        sw.onLink(this);
    }

}
