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
 * @version $Revision: #2 $ $Date: 2004/07/06 $
 **/

public class Schema {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/pdl/Schema.java#2 $ by $Author: vadim $, $DateTime: 2004/07/06 16:21:13 $";

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
