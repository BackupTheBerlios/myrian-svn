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
package com.redhat.persistence;

import com.redhat.persistence.oql.Expression;
import com.redhat.persistence.oql.Size;
import java.math.BigInteger;

/**
 * DataSet
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2004/06/07 $
 **/

public class DataSet {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/DataSet.java#1 $ by $Author: rhs $, $DateTime: 2004/06/07 13:49:55 $";

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
