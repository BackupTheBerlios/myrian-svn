package com.arsdigita.persistence.metadata;

import com.arsdigita.util.*;

import java.io.*;
import java.util.*;

/**
 * DDLWriter
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2002/08/09 $
 **/

public class DDLWriter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/DDLWriter.java#3 $ by $Author: rhs $, $DateTime: 2002/08/09 15:10:37 $";

    private File m_base;
    private boolean m_overwrite;

    public DDLWriter(String base) {
        this(new File(base), false);
    }

    public DDLWriter(String base, boolean overwrite) {
        this(new File(base), overwrite);
    }

    public DDLWriter(File base) {
        this(base, false);
    }

    public DDLWriter(File base, boolean overwrite) {
        m_base = base;
        m_overwrite = overwrite;
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

            File tab = new File(m_base, "table-" + table.getName() +
                                ".sql");
            File view = new File(m_base, "view-" + table.getName() +
                                 ".sql");
            if (!m_overwrite && (tab.exists() || view.exists())) {
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
        Set uncreated = new HashSet();
        Set created = new HashSet();
        Set deferred = new HashSet();
        List createOrder = new ArrayList();

        uncreated.addAll(tables);

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
            FileWriter writer = new FileWriter(new File(m_base,
                                                        "deferred.sql"));
            for (Iterator it = deferred.iterator(); it.hasNext(); ) {
                Table table = (Table) it.next();
                if (skipped.contains(table)) {
                    continue;
                }
                for (Iterator iter = table.getConstraints().iterator();
                     iter.hasNext(); ) {
                    Constraint con = (Constraint) iter.next();
                    if (con.isDeferred()) {
                        writer.write("alter table " + table.getName() +
                                     " add\n");
                        writer.write(con.getSQL());
                        writer.write(";\n");
                    }
                }
            }
            writer.close();
        }

        FileWriter writer = new FileWriter(new File(m_base, "create.sql"));
        for (Iterator it = createOrder.iterator(); it.hasNext(); ) {
            Table table = (Table) it.next();
            if (skipped.contains(table)) {
                //writer.write("@@table-" + table.getName() + ".sql\n");
            } else {
                writer.write("@@table-" + table.getName() + "-auto.sql\n");
            }
        }
        if (deferred.size() > 0) {
            //writer.write("@@deferred.sql\n");
        }
        writer.close();

        Assert.assertEquals(tables.size(), created.size());
    }

}
