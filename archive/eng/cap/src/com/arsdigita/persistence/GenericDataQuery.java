/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.persistence;

import com.redhat.persistence.DataSet;
import com.redhat.persistence.Signature;
import com.redhat.persistence.common.ParseException;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.common.SQLParser;
import com.redhat.persistence.metadata.Model;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Role;
import com.redhat.persistence.metadata.SQLBlock;
import com.redhat.persistence.oql.Static;

import java.io.StringReader;
import java.util.Collections;

/**
 * GenericDataQuery
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2004/06/07 $
 */

public class GenericDataQuery extends DataQueryImpl {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/GenericDataQuery.java#1 $ by $Author: rhs $, $DateTime: 2004/06/07 13:49:55 $";

    public GenericDataQuery(Session s, String sql, String[] columns) {
        super(s, ds(s, sql, columns));
    }

    private static DataSet ds(Session s, String sql, String[] columns) {
	ObjectType propType = s.getRoot().getObjectType("global.Object");
        final ObjectType type =
            new ObjectType(Model.getInstance("gdq"), sql, null);
	Signature sig = new Signature(type);
	for (int i = 0; i < columns.length; i++) {
	    type.addProperty
                (new Role(columns[i], propType, false, false, true));
	    sig.addPath(Path.get(columns[i]));
	}

	SQLParser p = new SQLParser(new StringReader(sql));
	try {
	    p.sql();
	} catch (ParseException e) {
	    throw new PersistenceException(e);
	}

        Static st = new Static
            (p.getSQL(), columns, false, Collections.EMPTY_MAP) {
            protected boolean hasType() { return true; }
            protected ObjectType getType() { return type; }
        };

        return new DataSet(s.getProtoSession(), sig, st);
    }

}
