/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * AbstractJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/07/22 $
 **/

public abstract class AbstractJoin extends Expression {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/oql/AbstractJoin.java#2 $ by $Author: richardl $, $DateTime: 2004/07/22 15:13:20 $";

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
        QFrame frame = gen.frame(this, join(left.getType(), right.getType()));
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
