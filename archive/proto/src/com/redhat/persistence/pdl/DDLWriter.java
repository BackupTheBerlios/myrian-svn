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

package com.redhat.persistence.pdl;

import com.redhat.persistence.metadata.*;

import com.arsdigita.util.Assert;

import com.arsdigita.db.DbHelper;
import com.arsdigita.persistence.Utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.*;

/**
 * DDLWriter
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public class DDLWriter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/pdl/DDLWriter.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    private File m_base;
    private boolean m_overwrite;
    private Set m_files;

    private boolean m_isTestPDL;

    public DDLWriter(String base,
                     Set files) {
        this(new File(base), files, false);
    }

    public DDLWriter(String base,
                     Set files,
                     boolean overwrite) {
        this(new File(base), files, overwrite);
    }

    public DDLWriter(File base,
                     Set files) {
        this(base, files, false);
    }

    public DDLWriter(File base,
                     Set files,
                     boolean overwrite) {
        if (!base.isDirectory()) {
            throw new IllegalArgumentException("Expecting directory. " + base.getAbsolutePath() + " is not a directory.");
        }
        m_base = base;
        m_overwrite = overwrite;
        m_files = files;
    }

    public void setTestPDL(boolean isTestPDL) {
        m_isTestPDL = isTestPDL;
    }
    public void write(Root root) throws IOException {
        write(root.getTables());
    }

    public void write(Collection tables)
        throws IOException {
        Set skipped = new HashSet();

        for (Iterator it = tables.iterator(); it.hasNext(); ) {
            Table table = (Table) it.next();

            String tableFile = "table-" + table.getName() + ".sql";
            String viewFile = "view-" + table.getName() + ".sql";

            if (!m_overwrite &&
                (m_files.contains(tableFile) || m_files.contains(viewFile))) {
                skipped.add(table);
                continue;
            }

            File file = new File(m_base, "table-" + table.getName() +
                                 "-auto.sql");

            FileWriter writer = new FileWriter(file);
            writer.write(table.getSQL());
            writer.write(";\n");
            writer.close();
        }

        Set deps = new HashSet();
        List uncreated = new ArrayList();
        Set created = new HashSet();
        Set deferred = new HashSet();
        List createOrder = new ArrayList();

        uncreated.addAll(tables);
        Collections.sort(uncreated, new Comparator() {
                public int compare(Object o1, Object o2) {
                    Table t1 = (Table) o1;
                    Table t2 = (Table) o2;
                    return t1.getName().compareTo(t2.getName());
                }
            });

        int before;

        do {
            before = created.size();

            for (Iterator it = uncreated.iterator(); it.hasNext(); ) {
                Table table = (Table) it.next();

                deps.clear();
                deps.addAll(table.getDependencies());
                deps.retainAll(tables);

                if (created.containsAll(deps)) {
                    it.remove();
                    created.add(table);
                    //if (table.isCircular()) {
                    deferred.add(table);
                    //}
                    createOrder.add(table);
                }
            }

        } while (created.size() > before);

        final boolean deferedConstraintsExist = deferred.size() > 0;
        if (deferedConstraintsExist) {
            writeDeferredSQL(deferred, skipped);
        }

        writeTableCreateAndDropSQL(createOrder, skipped, deferred);
        if (m_isTestPDL) {
            writeSetupAndTeardown(deferedConstraintsExist);
        }
        Assert.assertEquals(tables.size(), created.size());
    }

    private void writeSetupAndTeardown(boolean deferedConstraintsExist) throws IOException {
        FileWriter setup = new FileWriter(new File(m_base, "setup.sql"));
        FileWriter teardown = new FileWriter(new File(m_base, "teardown.sql"));

        try {
            setup.write(getDBFileLoadPrefix() + "create.sql" + Utilities.LINE_BREAK);
            if (deferedConstraintsExist) {
                setup.write(getDBFileLoadPrefix() + "deferred.sql" + Utilities.LINE_BREAK);
                teardown.write(getDBFileLoadPrefix() + "drop-constraints.sql" + Utilities.LINE_BREAK);
            }
            teardown.write(getDBFileLoadPrefix() + "drop-tables.sql" + Utilities.LINE_BREAK);

        } finally {
            try {
                setup.close();
            } catch(IOException e) {
            }
            try {
                teardown.close();
            } catch(IOException e) {
            }

        }




    }

    private void writeDeferredSQL(Set deferred, Set skipped) throws IOException {
        List alters = new ArrayList();
        List dropConstraints = new ArrayList();

        for (Iterator it = deferred.iterator(); it.hasNext(); ) {
            Table table = (Table) it.next();
            if (skipped.contains(table)) {
                continue;
            }
            for (Iterator iter = table.getConstraints().iterator();
                 iter.hasNext(); ) {
                Constraint con = (Constraint) iter.next();
                if (con.isDeferred()) {
                    alters.add("alter table " + table.getName() +
                               " add " + Utilities.LINE_BREAK +
                               con.getSQL() + ";" +
                               Utilities.LINE_BREAK);
                    if (DbHelper.getDatabase() == DbHelper.DB_POSTGRES) {
                        dropConstraints.add("alter table " + table.getName() +
                                            Utilities.LINE_BREAK +
                                            " drop constraint " +
                                            con.getName() + " RESTRICT;" +
                                            Utilities.LINE_BREAK);
                    } else {
                        dropConstraints.add("alter table " + table.getName() +
                                            Utilities.LINE_BREAK +
                                            " drop constraint " +
                                            con.getName() + ";" +
                                            Utilities.LINE_BREAK);
                    }
                }
            }
        }

        Collections.sort(alters);
        Collections.sort(dropConstraints);

        FileWriter writer = new FileWriter(new File(m_base,
                                                    "deferred.sql"));
        for (Iterator it = alters.iterator(); it.hasNext(); ) {
            writer.write((String) it.next());
        }
        writer.close();

        FileWriter dropFileWriter =
            new FileWriter(new File(m_base, "drop-constraints.sql"));

        for (Iterator it = dropConstraints.iterator(); it.hasNext(); ) {
            dropFileWriter.write((String) it.next());
        }
        dropFileWriter.close();
    }

    private void writeTableCreateAndDropSQL(List createOrder, Set skipped, Set deferred) throws IOException {
        FileWriter dropFileWriter =
            new FileWriter(new File(m_base, "drop-tables.sql"));
        FileWriter writer = new FileWriter(new File(m_base, "create.sql"));
        try {
            final String loaderPrefix = getDBFileLoadPrefix() + getDDLPath();
            for (Iterator it = createOrder.iterator(); it.hasNext(); ) {
                Table table = (Table) it.next();
                if (skipped.contains(table)) {
                    //writer.write("@@table-" + table.getName() + ".sql\n");
                } else {
                    writer.write(loaderPrefix + "table-" +
                                     table.getName() + "-auto.sql" +
                                     Utilities.LINE_BREAK);

                    // The order does not matter since we drop the constraints
                    // first
                    dropFileWriter.write("drop table " + table.getName() + ";" +
                                         Utilities.LINE_BREAK);
                }
            }

            if (deferred.size() > 0) {
                //writer.write("@@deferred.sql\n");
            }

        } finally {
            writer.close();
            dropFileWriter.close();
        }
    }

    private String getDBFileLoadPrefix() {
        if (DbHelper.getDatabase() == DbHelper.DB_POSTGRES) {
            return "\\i ";
        } else {
            return "@@ ";
        }

    }
    private String getDDLPath() {
        if (m_isTestPDL) {
            return "";
        }

        String dir = DbHelper.getDatabaseDirectory();
        String path = "ddl/" + dir + "/";
        return path;
    }
}

