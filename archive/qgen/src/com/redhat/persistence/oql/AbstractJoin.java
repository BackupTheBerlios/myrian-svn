package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * AbstractJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/30 $
 **/

public abstract class AbstractJoin extends Query {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/AbstractJoin.java#1 $ by $Author: rhs $, $DateTime: 2003/12/30 22:37:27 $";

    private Expression m_left;
    private Expression m_right;
    private Expression m_condition;

    AbstractJoin(Expression left, Expression right, Expression condition) {
        m_left = left;
        m_right = right;
        m_condition = condition;
    }

    public String toSQL() {
        return m_left.toSQL() + " " + getJoinType() + " join " +
            m_right.toSQL() +
            (m_condition == null ? "" : " on " + m_condition.toSQL());
    }

    abstract String getJoinType();

    void add(Environment env, Frame parent) {
        env.add(m_left, parent);
        env.add(m_right, parent);
        if (m_condition != null) {
            env.add(m_condition, env.getFrame(this));
        }
    }

    void type(Environment env, Frame f) {
        Frame left = env.getFrame(m_left);
        Frame right = env.getFrame(m_right);
        if (left.getType() == null || right.getType() == null) { return; }
        f.setType(join(left.getType(), right.getType()));
    }

    private static ObjectType join(ObjectType left, ObjectType right) {
        Model anon = Model.getInstance("anonymous.join");
        ObjectType result = new ObjectType
            (anon, left.getQualifiedName() + "$" + right.getQualifiedName(),
             null);
        addProperties(result, left);
        addProperties(result, right);
        Set lkeys = getKeys(left);
        Set rkeys = getKeys(right);
        for (Iterator it = lkeys.iterator(); it.hasNext(); ) {
            List lkey = (List) it.next();
            for (Iterator iter = rkeys.iterator(); iter.hasNext(); ) {
                List rkey = (List) iter.next();
                List key = new ArrayList();
                addToKey(result, key, lkey);
                addToKey(result, key, rkey);
                addKey(result, key);
            }
        }
        return result;
    }

    private static void addToKey(ObjectType type, List key, List from) {
        for (Iterator it = from.iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            key.add(type.getProperty(prop.getName()));
        }
    }

    private static void addProperties(ObjectType to, ObjectType from) {
        for (Iterator it = from.getProperties().iterator(); it.hasNext(); ) {
            to.addProperty(copy((Property) it.next()));
        }
    }

    private static Property copy(Property prop) {
        final Property[] result = { null };
        prop.dispatch(new Property.Switch() {
            public void onRole(Role role) {
                result[0] = new Role(role.getName(), role.getType(),
                                     role.isComponent(), role.isCollection(),
                                     role.isNullable());
            }
            public void onLink(Link link) {
                throw new Error("not implemented yet");
            }
            public void onAlias(Alias alias) {
                throw new Error("not implemented yet");
            }
        });
        return result[0];
    }

    void count(Environment env, Frame f) {
        Frame left = env.getFrame(m_left);
        Frame right = env.getFrame(m_right);
        Frame condition = m_condition == null ?
            null : env.getFrame(m_condition);

        int c = Math.max(left.getCorrelationMax(), right.getCorrelationMax());
        if (condition != null) {
            c = Math.max(c, condition.getCorrelationMax() - 1);
        }
        f.setCorrelationMax(c);

        c = Math.min(left.getCorrelationMin(), right.getCorrelationMin());
        if (condition != null) {
            c = Math.min(c, condition.getCorrelationMin());
        }
        f.setCorrelationMin(c);

        if (left.isSet() && right.isSet()) {
            f.addAllKeys(getKeys(f.getType()));
        }
    }

    public String toString() {
        return getJoinType() + "(" + m_left + ", " + m_right +
            (m_condition == null ? "" : ", " + m_condition) + ")";
    }

}
