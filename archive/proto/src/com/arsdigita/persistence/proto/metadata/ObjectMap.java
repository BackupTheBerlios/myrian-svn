package com.arsdigita.persistence.proto.metadata;

import com.arsdigita.persistence.proto.common.*;

import java.util.*;

/**
 * ObjectMap
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2003/01/28 $
 **/

public class ObjectMap extends Element {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/ObjectMap.java#6 $ by $Author: rhs $, $DateTime: 2003/01/28 19:17:39 $";

    private ObjectType m_type;
    private Mist m_mappings = new Mist(this);
    private ArrayList m_key = new ArrayList();

    public ObjectMap(ObjectType type) {
        m_type = type;
    }

    public Root getRoot() {
        return (Root) getParent();
    }

    ObjectMap getSuperMap() {
        if (m_type.getSupertype() == null) {
            return null;
        } else {
            return getRoot().getObjectMap(m_type.getSupertype());
        }
    }

    public ObjectType getObjectType() {
        return m_type;
    }

    public boolean hasMapping(Path p) {
        if (m_mappings.containsKey(p)) {
            return true;
        } else {
            ObjectMap sm = getSuperMap();
            if (sm == null) {
                return false;
            } else {
                return sm.hasMapping(p);
            }
        }
    }

    public Mapping getMapping(Path p) {
        if (m_mappings.containsKey(p)) {
            return (Mapping) m_mappings.get(p);
        } else {
            ObjectMap sm = getSuperMap();
            if (sm == null) {
                return null;
            } else {
                return sm.getMapping(p);
            }
        }
    }

    public void addMapping(Mapping mapping) {
        m_mappings.add(mapping);
    }

    private void getMappings(ArrayList result) {
        ObjectMap sm = getSuperMap();
        if (sm != null) {
            sm.getMappings(result);
        }
        result.addAll(m_mappings);
    }

    public Collection getMappings() {
        ArrayList result = new ArrayList();
        getMappings(result);
        return result;
    }

    public Collection getKeyProperties() {
        ObjectMap sm = getSuperMap();
        if (sm == null) {
            return m_key;
        } else {
            return sm.getKeyProperties();
        }
    }

    public Collection getFetchedPaths() {
        HashSet result = new HashSet();
        for (Iterator it = getMappings().iterator(); it.hasNext(); ) {
            Mapping m = (Mapping) it.next(); 
            if (m.isValue()) {
                result.add(m.getPath());
            }
        }

        throw new Error("need to add aggressive loads in here");
    }

    public Join getSuperJoin() {
        throw new Error("not implemented");
    }

    public Collection getJoins() {
        throw new Error("not implemented");
    }

    public int getRank(Table table) {
        throw new Error("not implemented");
    }

    Object getKey() {
        return getObjectType();
    }

}
