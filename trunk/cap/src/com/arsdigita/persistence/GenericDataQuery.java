/*
 * Copyright (C) 2001-2004 Red Hat, Inc. All Rights Reserved.
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
package com.arsdigita.persistence;

import org.myrian.persistence.DataSet;
import org.myrian.persistence.Signature;
import org.myrian.persistence.common.ParseException;
import org.myrian.persistence.common.Path;
import org.myrian.persistence.common.SQLParser;
import org.myrian.persistence.metadata.*;

import java.io.StringReader;
import java.util.Collections;

/**
 * GenericDataQuery
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 */

public class GenericDataQuery extends DataQueryImpl {


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
        org.myrian.persistence.oql.Static st =
            new org.myrian.persistence.oql.Static
            (p.getSQL(), columns, false, Collections.EMPTY_MAP) {
                protected boolean hasMap() { return true; }
                protected ObjectMap getMap() { return map; }
            };

        return new DataSet(s.getProtoSession(), sig, st);
    }

}
