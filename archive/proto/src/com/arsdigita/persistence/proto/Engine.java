package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.Property;

/**
 * Engine
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2003/01/06 $
 **/

public abstract class Engine {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Engine.java#5 $ by $Author: rhs $, $DateTime: 2003/01/06 17:58:56 $";

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

    protected abstract Filter getAnd(Filter left, Filter right);

    protected abstract Filter getOr(Filter left, Filter right);

    protected abstract Filter getNot(Filter operand);

    protected abstract Filter getEquals(Path left, Path right);

    protected abstract Filter getIn(Path path, Query query);

    protected abstract Filter getContains(Path collection, Path element);

}
