package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import org.apache.log4j.Logger;

import java.util.*;

/**
 * Operation
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #11 $ $Date: 2003/04/30 $
 **/

abstract class Operation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Operation.java#11 $ by $Author: rhs $, $DateTime: 2003/04/30 10:11:14 $";

    private static final Logger LOG = Logger.getLogger(Operation.class);

    private Environment m_env;
    private HashMap m_types = new HashMap();
    private HashSet m_parameters = new HashSet();
    private HashMap m_mappings = new HashMap();

    protected Operation(Environment env) {
        m_env = env;
    }

    protected Operation() {
        this(new Environment());
    }

    public boolean isParameter(Path path) {
        return m_parameters.contains(path);
    }

    public void addParameter(Path path) {
        m_parameters.add(path);
    }

    public boolean contains(Path parameter) {
        return m_env.contains(parameter);
    }

    public void set(Path parameter, Object value, int type) {
        m_parameters.add(parameter);
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

    public Path[] getMapping(Path p) {
        return (Path[]) m_mappings.get(p);
    }

    public void setMapping(Path p, Path[] cols) {
        m_mappings.put(p, cols);
    }

    public void setMappings(Map map) {
        m_mappings.putAll(map);
    }

    abstract void write(SQLWriter w);

    public String toString() {
        SQLWriter w = new ANSIWriter();
        w.write(this);
        return w.getSQL() + "\n" + w.getBindings();
    }

}
