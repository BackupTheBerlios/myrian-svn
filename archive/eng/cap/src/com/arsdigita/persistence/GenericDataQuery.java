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
import com.redhat.persistence.metadata.*;

import java.io.StringReader;
import java.util.Collections;

/**
 * GenericDataQuery
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/08/05 $
 */

public class GenericDataQuery extends DataQueryImpl {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/GenericDataQuery.java#2 $ by $Author: rhs $, $DateTime: 2004/08/05 12:04:47 $";

    public GenericDataQuery(Session s, String sql, String[] columns) {
        super(s, ds(s, sql, columns));
    }

    private static DataSet ds(Session s, String sql, String[] columns) {
	ObjectType propType = s.getRoot().getObjectType("global.Object");
        ObjectType type = new ObjectType(Model.getInstance("gdq"), sql, null);
        final ObjectMap map = new ObjectMap(type);
	Signature sig = new Signature(type);
	for (int i = 0; i < columns.length; i++) {
	    type.addProperty
                (new Role(columns[i], propType, false, false, true));
            Mapping m = new Static(Path.get(columns[i]));
            m.setMap(new ObjectMap(propType));
            map.addMapping(m);
	    sig.addPath(Path.get(columns[i]));
	}

	SQLParser p = new SQLParser(new StringReader(sql));
	try {
	    p.sql();
	} catch (ParseException e) {
	    throw new PersistenceException(e);
	}

        // XXX: now that oql runs on object maps we could actually put
        // the sql in the object map's retrieve all rather than using
        // a static.
        com.redhat.persistence.oql.Static st =
            new com.redhat.persistence.oql.Static
            (p.getSQL(), columns, false, Collections.EMPTY_MAP) {
                protected boolean hasMap() { return true; }
                protected ObjectMap getMap() { return map; }
            };

        return new DataSet(s.getProtoSession(), sig, st);
    }

}
