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
package org.myrian.persistence;

import org.myrian.persistence.oql.Expression;
import org.myrian.persistence.oql.Size;
import java.math.BigInteger;

/**
 * DataSet
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2004/10/01 $
 **/

public class DataSet {

    public final static String versionId = "$Id: //eng/persistence/dev/src/org/myrian/persistence/DataSet.java#1 $ by $Author: vadim $, $DateTime: 2004/10/01 18:41:18 $";

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
