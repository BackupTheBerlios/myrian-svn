package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * JoinTypeNode
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/01/23 $
 **/

class JoinTypeNode extends TypeNode {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/JoinTypeNode.java#2 $ by $Author: rhs $, $DateTime: 2004/01/23 15:34:30 $";

    private TypeNode m_left;
    private TypeNode m_right;

    public JoinTypeNode(TypeNode left, TypeNode right) {
        m_left = left;
        m_right = right;
        add(m_left);
        add(m_right);
    }

    void updateType() {
        if (m_left.type != null && m_right.type != null) {
            type = join(m_left.type, m_right.type);
        }
    }

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
        Set lkeys = Expression.getKeys(left);
        Set rkeys = Expression.getKeys(right);
        for (Iterator it = lkeys.iterator(); it.hasNext(); ) {
            List lkey = (List) it.next();
            for (Iterator iter = rkeys.iterator(); iter.hasNext(); ) {
                List rkey = (List) iter.next();
                List key = new ArrayList();
                addToKey(result, key, lkey);
                addToKey(result, key, rkey);
                Expression.addKey(result, key);
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

}
