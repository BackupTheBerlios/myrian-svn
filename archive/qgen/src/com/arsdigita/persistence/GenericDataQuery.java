/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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

import com.redhat.persistence.Signature;
import com.redhat.persistence.common.ParseException;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.common.SQLParser;
import com.redhat.persistence.metadata.Model;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Role;
import com.redhat.persistence.metadata.SQLBlock;
import java.io.StringReader;
import com.redhat.persistence.oql.Static;

/**
 * GenericDataQuery
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/02/24 $
 */

public class GenericDataQuery extends DataQueryImpl {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/arsdigita/persistence/GenericDataQuery.java#2 $ by $Author: ashah $, $DateTime: 2004/02/24 12:49:36 $";

    private SQLBlock m_block;

    public GenericDataQuery(Session s, String sql, String[] columns) {
        super(s, sig(s, sql, columns), new Static(sql));
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

    private static final Signature sig(Session s, String sql, String[] paths) {
	ObjectType propType = s.getRoot().getObjectType("global.Object");
	ObjectType type = new ObjectType(Model.getInstance("gdq"), sql, null);
	Signature sig = new Signature(type);
	for (int i = 0; i < paths.length; i++) {
	    type.addProperty
		(new Role(paths[i], propType, false, false, true));
	    sig.addPath(Path.get(paths[i]));
	}
        return sig;
    }

}
