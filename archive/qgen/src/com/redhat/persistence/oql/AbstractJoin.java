package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * AbstractJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #9 $ $Date: 2004/02/24 $
 **/

public abstract class AbstractJoin extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/AbstractJoin.java#9 $ by $Author: rhs $, $DateTime: 2004/02/24 10:13:24 $";

    private Expression m_left;
    private Expression m_right;
    private Expression m_condition;

    AbstractJoin(Expression left, Expression right, Expression condition) {
        m_left = left;
        m_right = right;
        m_condition = condition;
    }

    void frame(Generator gen) {
        m_left.frame(gen);
        QFrame left = gen.getFrame(m_left);
        m_right.frame(gen);
        QFrame right = gen.getFrame(m_right);
        QFrame frame = gen.frame
            (this, join(left.getType(), right.getType()));
        frame.addChild(left);
        frame.addChild(right);
        List values = new ArrayList();
        values.addAll(left.getValues());
        values.addAll(right.getValues());
        frame.setValues(values);
        gen.addUses(this, gen.getUses(m_left));
        gen.addUses(this, gen.getUses(m_right));
        if (m_condition != null) {
            gen.push(frame);
            try {
                m_condition.frame(gen);
            } finally {
                gen.pop();
            }
            frame.setCondition(m_condition);
            gen.addUses(this, gen.getUses(m_condition));
        }
    }

    String emit(Generator gen) {
        return gen.getFrame(this).emit();
    }

    public String toString() {
        return getJoinType() + "(" + m_left + ", " + m_right +
            (m_condition == null ? "" : ", " + m_condition) + ")";
    }

    abstract String getJoinType();

    String summary() { return getJoinType(); }

    static ObjectType join(final ObjectType left, final ObjectType right) {
        Model anon = Model.getInstance("anonymous.join");
        ObjectType result = new ObjectType
            (anon, left.getQualifiedName() + "$" + right.getQualifiedName(),
             null) {
            public String toString() {
                return left + " + " + right;
            }
            public List getKeyProperties() {
                ArrayList result = new ArrayList();
                result.addAll(getProperties());
                return result;
            }
        };
        addProperties(result, left);
        addProperties(result, right);
        return result;
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

}
