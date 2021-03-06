package com.redhat.persistence.metadata;

import com.redhat.persistence.common.*;
import java.util.*;

/**
 * Mapping
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public abstract class Mapping extends Element {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/metadata/Mapping.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    public static abstract class Switch {
        public abstract void onValue(Value m);
        public abstract void onJoinTo(JoinTo m);
        public abstract void onJoinFrom(JoinFrom m);
        public abstract void onJoinThrough(JoinThrough m);
        public abstract void onStatic(Static m);
    }

    private Path m_path;
    private SQLBlock m_retrieve;
    private ArrayList m_adds = null;
    private ArrayList m_removes = null;

    protected Mapping(Path path) {
        m_path = path;
    }

    public ObjectMap getObjectMap() {
        return (ObjectMap) getParent();
    }

    public Path getPath() {
        return m_path;
    }

    public abstract Table getTable();

    public SQLBlock getRetrieve() {
        return m_retrieve;
    }

    public void setRetrieve(SQLBlock retrieve) {
        m_retrieve = retrieve;
    }

    public Collection getAdds() {
        return m_adds;
    }

    public void setAdds(Collection adds) {
        if (adds == null) {
            m_adds = null;
        } else {
            m_adds = new ArrayList();
            m_adds.addAll(adds);
        }
    }

    public Collection getRemoves() {
        return m_removes;
    }

    public void setRemoves(Collection removes) {
        if (removes == null) {
            m_removes = null;
        } else {
            m_removes = new ArrayList();
            m_removes.addAll(removes);
        }
    }

    public abstract void dispatch(Switch sw);

    Object getElementKey() {
        return getPath();
    }

    public String toString() {
        return "<mapping for " + m_path + ">";
    }

}
