/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.redhat.persistence;

import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Property;
import com.redhat.persistence.oql.Query;

/**
 * QuerySource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/03/30 $
 **/

public class QuerySource {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/QuerySource.java#6 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

    private Session m_ssn = null;

    void setSession(Session ssn) {
        m_ssn = ssn;
    }

    public Session getSession() {
        return m_ssn;
    }

    public Query getQuery(ObjectType type) {
        throw new UnsupportedOperationException();
    }

    public Query getQuery(PropertyMap keys) {
        throw new UnsupportedOperationException();
    }

    // These should probably be changed to take type signatures.
    public Query getQuery(Object obj) {
        throw new UnsupportedOperationException();
    }

    public Query getQuery(Object obj, Property prop) {
        throw new UnsupportedOperationException();
    }

}
