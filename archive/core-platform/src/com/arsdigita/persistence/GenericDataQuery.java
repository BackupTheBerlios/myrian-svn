/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.persistence;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;
import com.arsdigita.persistence.proto.Cursor;
import com.arsdigita.persistence.proto.Query;
import com.arsdigita.persistence.proto.RecordSet;
import com.arsdigita.persistence.proto.Signature;

import java.io.StringReader;

/**
 * GenericDataQuery
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #8 $ $Date: 2003/06/26 $
 */

public class GenericDataQuery extends DataQueryImpl {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/GenericDataQuery.java#8 $ by $Author: rhs $, $DateTime: 2003/06/26 18:40:22 $";

    private SQLBlock m_block;

    public GenericDataQuery(Session s, String sql, String[] columns) {
        super(s, query(sql, columns));
	SQLParser p = new SQLParser(new StringReader(sql));
	try {
	    p.sql();
	} catch (ParseException e) {
	    throw new PersistenceException(e);
	}
	m_block = new SQLBlock(p.getSQL());
	for (int i = 0; i < columns.length; i++) {
	    Path path = Path.get(columns[i]);
	    m_block.addMapping(path, path);
	}
    }

    private static final Query query(String sql, String[] paths) {
	ObjectType propType = Root.getRoot().getObjectType("global.Object");
	ObjectType type = new ObjectType(Model.getInstance("gdq"), sql, null);
	Signature sig = new Signature(type);
	for (int i = 0; i < paths.length; i++) {
	    type.addProperty
		(new Role(paths[i], propType, false, false, true));
	    sig.addPath(Path.get(paths[i]));
	}
	return new Query(sig, null);
    }

    protected Cursor execute(final Query query) {
	return new Cursor(getSession().getProtoSession(), query) {
		protected RecordSet execute() {
		    return GenericDataQuery.this.getSession().getEngine()
			.execute(query, m_block);
		}
	    };
    }

    public long size() {
        return getSession().getEngine().size(makeQuery(), m_block);
    }

}
