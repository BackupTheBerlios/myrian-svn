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
package com.redhat.persistence;

import com.redhat.persistence.oql.Expression;
import com.redhat.persistence.oql.Size;
import java.math.BigInteger;

/**
 * DataSet
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class DataSet {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/DataSet.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private Session m_ssn;
    private Signature m_sig;
    private Expression m_expr;

    public DataSet(Session ssn, Signature sig, Expression expr) {
        m_ssn = ssn;
        m_sig = sig;
        setExpression(expr);
    }

    public Session getSession() {
        return m_ssn;
    }

    public Signature getSignature() {
        return m_sig;
    }

    public Expression getExpression() {
        return m_expr;
    }

    void setExpression(Expression expr) {
        // XXX: type check
        m_expr = expr;
    }

    public Cursor getCursor() {
        return new Cursor(this);
    }

    public long size() {
        getSession().flush();
        return getSession().getEngine().size(m_expr);
    }

    public boolean isEmpty() {
        return size() == 0L;
    }
}
