package com.arsdigita.persistence.proto.metadata;

/**
 * Link
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/31 $
 **/

public class Link extends Property {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/Link.java#1 $ by $Author: rhs $, $DateTime: 2002/12/31 15:39:17 $";

    private Path m_path;

    public Link(String name, Path path) {
        super(name);
        m_path = path;
    }

    public ObjectType getType() {
        throw new Error("Not implemented");
    }

}
