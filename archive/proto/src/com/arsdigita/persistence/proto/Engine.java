package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.Property;

/**
 * Engine
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/01/02 $
 **/

public abstract class Engine {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Engine.java#3 $ by $Author: rhs $, $DateTime: 2003/01/02 15:38:03 $";

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

    protected abstract Filter getAnd(Filter leftOperand, Filter rightOperand);

    protected abstract Filter getOr(Filter leftOperand, Filter rightOperand);

    protected abstract Filter getNot(Filter operand);

    protected abstract Filter getEquals(Path path, Object value);

    protected abstract Filter getIn(Path path, Query query);

    protected abstract Filter getContains(Path path, Object value);

}
