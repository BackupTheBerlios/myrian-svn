package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import org.apache.log4j.Logger;

import java.util.*;

/**
 * Operation
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #9 $ $Date: 2003/03/11 $
 **/

abstract class Operation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Operation.java#9 $ by $Author: rhs $, $DateTime: 2003/03/11 10:49:54 $";

    private static final Logger LOG = Logger.getLogger(Operation.class);

    private Environment m_env;
    private HashMap m_types = new HashMap();

    protected Operation(Environment env) {
        m_env = env;
    }

    protected Operation() {
        this(new Environment());
    }

    public boolean isParameter(Path path) {
        return m_env.isParameter(path);
    }

    public void set(Path parameter, Object value, int type) {
        m_env.set(parameter, value);
        m_types.put(parameter, new Integer(type));
    }

    public Object get(Path parameter) {
        return m_env.get(parameter);
    }

    public int getType(Path parameter) {
        return ((Integer) m_types.get(parameter)).intValue();
    }

    Environment getEnvironment() {
        return m_env;
    }

    abstract void write(SQLWriter w);

    public String toString() {
        SQLWriter w = new ANSIWriter();
        w.write(this);
        return w.getSQL() + "\n" + w.getBindings();
    }

}
