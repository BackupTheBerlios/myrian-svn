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

import org.myrian.persistence.Session;
import org.myrian.persistence.common.Path;
import org.myrian.persistence.common.TokenMgrError;
import org.myrian.persistence.metadata.*;
import java.io.*;
import java.util.*;

/**
 * Expression
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/10/04 $
 **/

public abstract class Expression {


    public static Expression valueOf(Path path) {
        if (path.getParent() == null) {
            return new Variable(path.getName());
        } else {
            return new Get(valueOf(path.getParent()), path.getName());
        }
    }

    public static Expression valueOf(String expression) {
        return valueOf(expression, Collections.EMPTY_MAP);
    }

    public static Expression valueOf(String expression, Map parameters) {
        OQLParser p = new OQLParser(new StringReader(expression), parameters);
        try {
            return p.expression();
        } catch (ParseException e) {
            throw new Error(expression, e);
        } catch (TokenMgrError e) {
            throw new Error(expression, e);
        }
    }

    public ObjectMap getMap(Session ssn) {
        Generator gen = Generator.getThreadGenerator();
        try {
            gen.init(ssn);
            frame(gen);
            return gen.getFrame(this).getMap();
        } catch (Throwable t) {
            throw new Error
                ("error during expression analysis: " + this, t);
        } finally {
            gen.clear();
        }
    }

    public ObjectType getType(Session ssn) {
        return getMap(ssn).getObjectType();
    }

    abstract void frame(Generator generator);

    abstract Code emit(Generator generator);

    abstract void hash(Generator generator);

    abstract String summary();

    // XXX: consider splitting this out into a separate interface
    static abstract class Emitter extends Expression {
        void frame(Generator gen) {};
        void hash(Generator gen) {};
        String summary() {
            return toString();
        }
    }

}
