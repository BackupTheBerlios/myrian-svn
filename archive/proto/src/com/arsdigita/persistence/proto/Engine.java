package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.Property;

/**
 * Engine
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2003/01/13 $
 **/

public abstract class Engine {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Engine.java#6 $ by $Author: rhs $, $DateTime: 2003/01/13 16:40:35 $";

    static final Engine getInstance(Session ssn) {
        return new com.arsdigita.persistence.proto.engine.MemoryEngine(ssn);
    }

    private Session m_ssn;

    protected Engine(Session ssn) {
        m_ssn = ssn;
    }

    public Session getSession() {
        return m_ssn;
    }

    protected abstract void commit();

    protected abstract void rollback();

    protected abstract RecordSet execute(Query query);

    protected abstract EventHandler getEventHandler();

    protected abstract void flush();

}
