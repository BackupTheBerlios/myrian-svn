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

package com.arsdigita.persistence.metadata;

import com.arsdigita.util.Assert;

import com.arsdigita.db.DbHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Comparator;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * DDLWriter
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #8 $ $Date: 2002/08/14 $
 **/

public class DDLWriter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/DDLWriter.java#8 $ by $Author: dennis $, $DateTime: 2002/08/14 23:39:40 $";

    private File m_base;
    private boolean m_overwrite;
    private Set m_files;

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
        m_base = base;
        m_overwrite = overwrite;
        m_files = files;
        if (!m_base.isDirectory()) {
            throw new IllegalArgumentException("expecting directory");
        }
    }

    public void write(MetadataRoot root) throws IOException {
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
            writer.write("\n");
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

        if (deferred.size() > 0) {
            List alters = new ArrayList();

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
                                   " add\n" + con.getSQL() + ";\n");
                    }
                }
            }

            Collections.sort(alters);

            FileWriter writer = new FileWriter(new File(m_base,
                                                        "deferred.sql"));
            for (Iterator it = alters.iterator(); it.hasNext(); ) {
                writer.write((String) it.next());
            }
            writer.close();
        }

        FileWriter writer = new FileWriter(new File(m_base, "create.sql"));
        for (Iterator it = createOrder.iterator(); it.hasNext(); ) {
            Table table = (Table) it.next();
            if (skipped.contains(table)) {
                //writer.write("@@table-" + table.getName() + ".sql\n");
            } else {
                String dir = DbHelper.getDatabaseDirectory();
                if (DbHelper.getDatabase() == DbHelper.DB_POSTGRES) {
                    writer.write("\\i ddl/" + dir + "/table-" +
                                 table.getName() + "-auto.sql\n");
                } else {
                    writer.write("@ ddl/" + dir + "/table-" +
                                 table.getName() + "-auto.sql\n");
                }
            }
        }
        if (deferred.size() > 0) {
            //writer.write("@@deferred.sql\n");
        }
        writer.close();

        Assert.assertEquals(tables.size(), created.size());
    }

}
