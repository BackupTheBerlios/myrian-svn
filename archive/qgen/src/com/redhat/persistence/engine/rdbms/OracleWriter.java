/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
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
 *
 */

package com.redhat.persistence.engine.rdbms;

import java.sql.Types;
import java.util.Collection;
import java.util.HashSet;

/**
 * OracleWriter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/02/24 $
 **/

public class OracleWriter extends ANSIWriter {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/engine/rdbms/OracleWriter.java#2 $ by $Author: ashah $, $DateTime: 2004/02/24 12:49:36 $";

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
