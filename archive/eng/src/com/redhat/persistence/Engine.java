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


/**
 * Engine
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public abstract class Engine {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/Engine.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
