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

package com.redhat.persistence.metadata;

import com.redhat.persistence.common.*;

/**
 * DataOperation
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/19 $
 **/

public class DataOperation extends Element {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/metadata/DataOperation.java#2 $ by $Author: rhs $, $DateTime: 2003/08/19 22:28:24 $";

    private Path m_name;
    private SQLBlock m_sql;

    public DataOperation(Path name, SQLBlock sql) {
        m_name = name;
        m_sql = sql;
    }

    public Path getName() {
        return m_name;
    }

    public SQLBlock getSQL() {
        return m_sql;
    }

    Object getElementKey() {
        return m_name;
    }

    public String toString() {
        return "data operation " + getName() + " {\n" + getSQL() + " }\n";
    }

}
