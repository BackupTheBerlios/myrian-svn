package com.arsdigita.persistence.proto.metadata;

/**
 * ValueMapping
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/31 $
 **/

public class ValueMapping extends Mapping {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/ValueMapping.java#1 $ by $Author: rhs $, $DateTime: 2002/12/31 15:39:17 $";

    private Column m_column;

    public ValueMapping(Path path, Column column) {
        super(path);
        m_column = column;
    }

    public Column getColumn() {
        return m_column;
    }

}
