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
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

class InFilter extends SimpleFilter implements Filter {

    private static Logger s_log = Logger.getLogger(InFilter.class);

    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/InFilter.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
