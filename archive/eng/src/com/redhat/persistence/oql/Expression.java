/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.redhat.persistence.oql;

import com.redhat.persistence.Session;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.common.TokenMgrError;
import com.redhat.persistence.metadata.*;
import java.io.*;
import java.util.*;

/**
 * Expression
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #8 $ $Date: 2004/08/30 $
 **/

public abstract class Expression {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/oql/Expression.java#8 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
