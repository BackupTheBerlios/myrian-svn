/*
 * Copyright (C) 2003-2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.myrian.persistence.oql;

import org.myrian.persistence.metadata.*;
import org.myrian.persistence.metadata.Static;
import java.util.*;

/**
 * AbstractJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/10/04 $
 **/

public abstract class AbstractJoin extends Expression {


    private Expression m_left;
    private Expression m_right;

    AbstractJoin(Expression left, Expression right) {
        m_left = left;
        m_right = right;
    }

    AbstractJoin(Expression left, Expression right, Expression condition) {
        this(left, new Filter(right, condition));
    }

    void frame(Generator gen) {
        m_left.frame(gen);
        QFrame left = gen.getFrame(m_left);
        gen.push(left);
        try {
            m_right.frame(gen);
        } finally {
            gen.pop();
        }
        QFrame right = gen.getFrame(m_right);
        if (this instanceof LeftJoin) {
            right.setOuter(true);
        }
        QFrame frame = gen.frame(this, join(left.getMap(), right.getMap()));
        frame.addChild(left);
        frame.addChild(right);
        List values = new ArrayList();
        values.addAll(left.getValues());
        values.addAll(right.getValues());
        frame.setValues(values);
        frame.addMappings(left.getMappings());
        frame.addMappings(right.getMappings());
        gen.addUses(this, gen.getUses(m_left));
        gen.addUses(this, gen.getUses(m_right));
    }

    Code emit(Generator gen) {
        return gen.getFrame(this).emit();
    }

    void hash(Generator gen) {
        m_left.hash(gen);
        m_right.hash(gen);
        gen.hash(getClass());
    }

    public String toString() {
        return getJoinType() + "(" + m_left + ", " + m_right + ")";
    }

    abstract String getJoinType();

    String summary() { return getJoinType(); }

    static ObjectMap join(ObjectMap left, ObjectMap right) {
        ObjectMap result =
            new ObjectMap(join(left.getObjectType(), right.getObjectType()));
        addMappings(result, left);
        addMappings(result, right);
        Collection keys = result.getKeyProperties();
        addFrom(result, keys, left.getKeyProperties());
        addFrom(result, keys, right.getKeyProperties());
        return result;
    }

    private static void addFrom(ObjectMap om, Collection to,
                                Collection props) {
        ObjectType ot = om.getObjectType();
        for (Iterator it = props.iterator(); it.hasNext(); ) {
            Property p = (Property) it.next();
            to.add(ot.getProperty(p.getName()));
        }
    }

    private static void addMappings(ObjectMap to, ObjectMap from) {
        for (Iterator it = from.getMappings().iterator(); it.hasNext(); ) {
            to.addMapping(copy((Mapping) it.next()));
        }
    }

    private static Mapping copy(final Mapping m) {
        final Mapping[] result = { null };
        m.dispatch(new Mapping.Switch() {
            public void onValue(Value v) {
                result[0] = new Value(v.getPath(), v.getColumn());
            }
            public void onJoinTo(JoinTo j) {
                result[0] = new JoinTo(j.getPath(), j.getKey());
            }
            public void onJoinFrom(JoinFrom j) {
                result[0] = new JoinFrom(j.getPath(), j.getKey());
            }
            public void onJoinThrough(JoinThrough j) {
                result[0] =
                    new JoinThrough(j.getPath(), j.getFrom(), j.getTo());
            }
            public void onQualias(Qualias q) {
                result[0] = new Qualias(q.getPath(), q.getQuery());
            }
            public void onNested(Nested n) {
                result[0] = new Nested(n.getPath(), n.getMap());
            }
            public void onStatic(Static s) {
                result[0] = new Static(s.getPath());
            }
        });
        Mapping cp = result[0];
        cp.setRetrieve(m.getRetrieve());
        cp.setMap(m.getMap(), m.getMap().isNested());
        // don't bother copying write side static stuff
        return cp;
    }

    static ObjectType join(final ObjectType left, final ObjectType right) {
        Model anon = Model.getInstance("anonymous.join");
        ObjectType result = new ObjectType
            (anon, left.getQualifiedName() + "$" + right.getQualifiedName(),
             (ObjectType) null) {
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
