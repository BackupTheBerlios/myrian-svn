package com.arsdigita.persistence.metadata;

import com.arsdigita.util.*;

import java.io.*;
import java.util.*;

/**
 * DDLWriter
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/08/06 $
 **/

public class DDLWriter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/DDLWriter.java#1 $ by $Author: rhs $, $DateTime: 2002/08/06 16:54:58 $";

    private File m_base;

    public DDLWriter(String base) {
        this(new File(base));
    }

    public DDLWriter(File base) {
        m_base = base;
        if (!m_base.isDirectory()) {
            throw new IllegalArgumentException("expecting directory");
        }
    }

    public void write(MetadataRoot root) throws IOException {
        write(root.getTables());
    }

    public void write(Collection tables)
        throws IOException {
        for (Iterator it = tables.iterator(); it.hasNext(); ) {
            Table table = (Table) it.next();

            File file = new File(m_base, table.getName() + "-create.sql");
            FileWriter writer = new FileWriter(file);
            writer.write(table.getSQL());
            writer.write("\n");
            writer.close();
        }

        Set deps = new HashSet();
        Set uncreated = new HashSet();
        Set created = new HashSet();
        Set deffered = new HashSet();
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
                    if (table.isCircular()) {
                        deffered.add(table);
                    }
                    createOrder.add(table);
                }
            }

        } while (created.size() > before);

        if (deffered.size() > 0) {
            FileWriter writer = new FileWriter(new File(m_base,
                                                        "deffered.sql"));
            for (Iterator it = deffered.iterator(); it.hasNext(); ) {
                Table table = (Table) it.next();
                for (Iterator iter = table.getConstraints().iterator();
                     iter.hasNext(); ) {
                    Constraint con = (Constraint) iter.next();
                    if (con.isDeffered()) {
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
            writer.write("@@" + table.getName() + "-create.sql\n");
        }
        if (deffered.size() > 0) {
            writer.write("@@deffered.sql\n");
        }
        writer.close();

        Assert.assertEquals(tables.size(), created.size());
    }

}
