package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Variable
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/30 $
 **/

public class Variable extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Variable.java#1 $ by $Author: rhs $, $DateTime: 2003/12/30 22:37:27 $";

    private String m_name;

    Variable(String name) {
        m_name = name;
    }

    public String toSQL() {
        return m_name;
    }

    void add(Environment env, Frame parent) {
        // do nothing
    }

    void type(Environment env, Frame f) {
        Property prop = env.getProperty(f.getParent(), m_name);
        if (prop == null) { return; }
        f.setType(prop.getType());
    }

    void count(Environment env, Frame f) {
        Frame parent = f.getParent();

        int c = env.getCorrelation(parent, m_name);
        f.setCorrelationMax(env.getCorrelation(parent, m_name));
        if (c != Integer.MAX_VALUE) {
            f.setCorrelationMin(c);
        }

        Property prop = env.getProperty(parent, m_name);
        if (prop == null) { return; }

        if (prop.isCollection()) {
            f.addAllKeys(getKeys(prop.getType()));
        }

        f.setCollection(prop.isCollection());
        f.setNullable(prop.isNullable());

        f.getInjection().add(prop);
    }

    public String toString() {
        return m_name;
    }

}
