/*
 * Copyright (C) 2003, 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 */

package com.redhat.persistence;

import com.redhat.persistence.oql.Expression;


/**
 * Engine
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/02/24 $
 **/

public abstract class Engine {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/Engine.java#2 $ by $Author: ashah $, $DateTime: 2004/02/24 12:49:36 $";

    private Session m_ssn;

    void setSession(Session ssn) {
        m_ssn = ssn;
    }

    public Session getSession() {
        return m_ssn;
    }

    protected abstract void write(Event ev);
    protected abstract void flush();
    protected abstract void rollback();
    protected abstract void commit();
    protected abstract RecordSet execute(Signature sig, Expression expr);
    protected abstract long size(Expression expr);

}
