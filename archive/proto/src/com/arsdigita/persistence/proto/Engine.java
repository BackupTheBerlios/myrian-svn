package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.Property;

/**
 * Engine
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/06 $
 **/

public abstract class Engine {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Engine.java#1 $ by $Author: rhs $, $DateTime: 2002/12/06 17:55:29 $";

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

    protected abstract void write(Event event);

    protected abstract void flush();

    protected abstract Filter getAnd(Filter leftOperand, Filter rightOperand);

    protected abstract Filter getOr(Filter leftOperand, Filter rightOperand);

    protected abstract Filter getNot(Filter operand);

    protected abstract Filter getEquals(Path path, Object value);

    protected abstract Filter getIn(Path path, Query query);

    protected abstract Filter getContains(Path path, Object value);

    protected abstract CreateEvent getCreate(Session ssn, OID oid);

    protected abstract DeleteEvent getDelete(Session ssn, OID oid);

    protected abstract SetEvent getSet(Session ssn, OID oid, Property prop,
                                       Object argument);

    protected abstract AddEvent getAdd(Session ssn, OID oid, Property prop,
                                       Object argument);

    protected abstract RemoveEvent getRemove(Session ssn, OID oid,
                                             Property prop, Object argument);

}
