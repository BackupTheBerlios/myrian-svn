package com.arsdigita.persistence.proto.metadata;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;

/**
 * ReferenceMapping
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/01/17 $
 **/

public class ReferenceMapping extends Mapping {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/ReferenceMapping.java#3 $ by $Author: rhs $, $DateTime: 2003/01/17 11:07:02 $";

    private ArrayList m_joins = new ArrayList();

    public ReferenceMapping(Path path) {
        super(path);
    }

    private Property getProperty() {
        return getObjectMap().getObjectType().getProperty(getPath());

    }

    public boolean isJoinTo() {
        return m_joins.size() == 1 && !getProperty().isCollection();
    }

    public boolean isJoinFrom() {
        return m_joins.size() == 1 && getProperty().isCollection();
    }

    public boolean isJoinThrough() {
        return m_joins.size() == 2;
    }

    public Join getJoin(int index) {
        return (Join) m_joins.get(index);
    }

    public Collection getJoins() {
        return m_joins;
    }

    public void addJoin(Join join) {
        m_joins.add(join);
    }

    public void dispatch(Switch sw) {
        sw.onReference(this);
    }

}
