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
package com.redhat.persistence.engine.rdbms;

import java.sql.Types;
import java.util.Collection;
import java.util.HashSet;

/**
 * OracleWriter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/08/30 $
 **/

public class OracleWriter extends ANSIWriter {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/engine/rdbms/OracleWriter.java#3 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    public void write(Select select) {
        write(select.getQuery().generate(getEngine().getSession(), true));
    }

    void writeBind(Object o, int jdbcType) {
        if (o == null) {
            super.writeBind(o, jdbcType);
        } else {
            switch (jdbcType) {
            case Types.BLOB:
                write("empty_blob()");
                break;
            case Types.CLOB:
                write("empty_clob()");
                break;
            default:
                super.writeBind(o, jdbcType);
                break;
            }
        }
    }
}
