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
package com.arsdigita.persistence;

import com.arsdigita.db.DbHelper;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.MetadataException;
import com.redhat.persistence.metadata.ObjectMap;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.metadata.SQLBlock;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * InFilter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #11 $ $Date: 2004/03/30 $
 **/

class InFilter extends SimpleFilter implements Filter {

    private static Logger s_log = Logger.getLogger(InFilter.class);

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/InFilter.java#11 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

    InFilter(Root root, String property, String subqueryProperty,
             String query) {
        super(makeConditions(root, property, subqueryProperty, query));
        if (s_log.isDebugEnabled()) {
            s_log.debug("InFilter: " + property + " - " + subqueryProperty +
                        " - " + query);
        }
    }

    private static SQLBlock getBlock(Root root, String query) {
        ObjectType ot = root.getObjectType(query);
        ObjectMap map = root.getObjectMap(ot);
        if (map == null) {
            throw new PersistenceException("no such query: " + query);
        }
        return map.getRetrieveAll();
    }

    private static String makeConditions(Root root, String prop,
                                         String subProperty, String query) {
        SQLBlock block = getBlock(root, query);
        Path subProp;
        if (subProperty == null) {
            Iterator paths = block.getPaths().iterator();
            if (paths.hasNext()) {
                subProp = (Path) paths.next();
            } else {
                return prop + " in (" + query + ")";
            }

            if (paths.hasNext()) {
                throw new PersistenceException
                    ("subquery has more than one mapping");
            }
        } else {
            subProp = Path.get(subProperty);
        }

        Path subcol = block.getMapping(subProp);
        if (subcol == null) {
            throw new MetadataException
                (root, block, "no such path: " + subProp);
        }

        final int currentDB = DbHelper.getDatabase();
        final StringBuffer sb = new StringBuffer();

        if (currentDB == DbHelper.DB_POSTGRES) {
            sb.append("exists ( select RAW[subquery_id] from (select RAW[");
            sb.append(subcol.getPath());
            sb.append("] as RAW[subquery_id] from (");
            sb.append(query);
            sb.append(") RAW[insub1] ) RAW[insub2] where ");
            sb.append("RAW[insub2.subquery_id] = ");
            sb.append(prop).append(")");
        } else if (currentDB == DbHelper.DB_ORACLE) {
            sb.append(prop).append(" in (select RAW[");
            sb.append(subcol.getPath()).append("] from (");
            sb.append(query).append(") RAW[insub])");
        } else {
            throw new IllegalStateException
                ("Unknown database: " + DbHelper.getDatabaseName(currentDB));
        }
        return sb.toString();
    }
}
