/*
 * Copyright (C) 2004 Red Hat, Inc.  All Rights Reserved.
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
package com.redhat.persistence.pdl;

import com.redhat.persistence.metadata.*;
import com.arsdigita.db.DbHelper;

import java.sql.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * Schema
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/08/30 $
 **/

public class Schema {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/pdl/Schema.java#3 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
