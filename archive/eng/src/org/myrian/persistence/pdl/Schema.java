/*
 * Copyright (C) 2004 Red Hat, Inc. All Rights Reserved.
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
package org.myrian.persistence.pdl;

import org.myrian.persistence.metadata.*;
import org.myrian.db.DbHelper;

import java.sql.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * Schema
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/10/01 $
 **/

public class Schema {

    public final static String versionId = "$Id: //eng/persistence/dev/src/org/myrian/persistence/pdl/Schema.java#1 $ by $Author: vadim $, $DateTime: 2004/10/01 18:41:18 $";

    private final static Logger LOG = Logger.getLogger(Schema.class);

    private Schema() {}

    private static void addDeferred(List constraints, Table table) {
        for (Iterator it = table.getConstraints().iterator(); it.hasNext(); ) {
            Constraint con = (Constraint) it.next();
            if (con.isDeferred()) { constraints.add(con); }
        }
    }

    public static void load(Root root, Connection conn) throws SQLException {
        load(root.getTables(), conn);
    }

    public static void load(List tables, Connection conn)
        throws SQLException {
        // XXX: should eliminate use of global variable here
        DbHelper.setDatabase(DbHelper.getDatabase(conn));
        Statement stmt = conn.createStatement();
        try {
            List constraints = new ArrayList();
            for (int i = 0; i < tables.size(); i++) {
                Table table = (Table) tables.get(i);
                String sql = table.getSQL();
                LOG.debug(sql);
                stmt.execute(sql);
                addDeferred(constraints, table);
            }

            for (int i = 0; i < constraints.size(); i++) {
                Constraint con = (Constraint) constraints.get(i);
                String sql = "alter table " + con.getTable().getName() +
                    " add " + con.getSQL();
                LOG.debug(sql);
                stmt.execute(sql);
            }
        } finally {
            stmt.close();
        }
    }

    public static void unload(Root root, Connection conn) throws SQLException {
        unload(root.getTables(), conn);
    }

    public static void unload(List tables, Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        try {
            List constraints = new ArrayList();
            for (int i = 0; i < tables.size(); i++) {
                Table table = (Table) tables.get(i);
                addDeferred(constraints, table);
            }

            for (int i = 0; i < constraints.size(); i++) {
                Constraint con = (Constraint) constraints.get(i);
                String sql = "alter table " + con.getTable().getName() +
                    " drop constraint " + con.getName();
                LOG.debug(sql);
                stmt.execute(sql);
            }

            for (int i = 0; i < tables.size(); i++) {
                Table table = (Table) tables.get(i);
                String sql = "drop table " + table.getName();
                LOG.debug(sql);
                stmt.execute(sql);
            }
        } finally {
            stmt.close();
        }
    }

}
