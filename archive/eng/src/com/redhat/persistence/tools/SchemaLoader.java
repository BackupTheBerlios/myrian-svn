package com.redhat.persistence.tools;

import com.arsdigita.util.jdbc.*;
import com.redhat.persistence.jdo.Extensions;

import java.io.*;
import java.sql.*;
import java.util.*;
import javax.jdo.*;
import javax.jdo.spi.*;

/**
 * SchemaLoader
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/09/22 $
 **/

public class SchemaLoader {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/tools/SchemaLoader.java#1 $ by $Author: rhs $, $DateTime: 2004/09/22 18:18:11 $";

    private static final void die(String message) {
        System.err.println(message);
        System.exit(1);
    }

    public static final void main(String[] argv) throws Exception {
        Properties props = new Properties();

        boolean load = true;

        List args = new ArrayList(Arrays.asList(argv));
        for (Iterator it = args.iterator(); it.hasNext(); ) {
            String arg = (String) it.next();
            if (!arg.startsWith("-")) { continue; }
            if (arg.equals("-load")) {
                load = true;
            } else if (arg.equals("-unload")) {
                load = false;
            } else {
                die("unrecognized option: " + arg);
            }
            it.remove();
        }

        if (args.size() == 0) {
            die("usage: SchemaLoader [ -load | -unload ] " +
                "<directory_1> ... <directory_n>");
        }

        InputStream is =
            SchemaLoader.class.getResourceAsStream("/jdo.properties");
        if (is == null) {
            die("cannot locate jdo.properties");
        }
        props.load(is);
        PersistenceManagerFactory pmf =
            JDOHelper.getPersistenceManagerFactory(props);
        Connection conn = Connections.acquire(pmf.getConnectionURL());
        conn.setAutoCommit(false);

        List classes = new ArrayList();
        for (Iterator it = args.iterator(); it.hasNext(); ) {
            File dir = new File((String) it.next());
            findClasses(dir, classes);
        }

        if (classes.isEmpty()) {
            die("no classes found");
        }

        for (int i = 0; i < classes.size(); i++) {
            System.out.println("Found: " + classes.get(i));
        }

        System.out.print((load ? "L" : "Unl") + "oading schema...");
        System.out.flush();
        if (load) {
            Extensions.load(classes, conn);
        } else {
            Extensions.unload(classes, conn);
        }
        conn.commit();
        System.out.println("done.");
    }

    private static void findClasses(File dir, List classes) {
        findClasses(dir, classes, "");
    }

    private static void findClasses(File dir, List classes, String prefix) {
        File[] files = dir.listFiles(new FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".class");
            }
        });

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                String newPrefix;
                if (prefix.equals("")) {
                    newPrefix = files[i].getName();
                } else {
                    newPrefix = prefix + "." + files[i].getName();
                }
                findClasses(files[i], classes, newPrefix);
            } else {
                String name = files[i].getName();
                name = name.substring(0, name.length() - 6);
                String qname;
                if (prefix.equals("")) {
                    qname = name;
                } else {
                    qname = prefix + "." + name;
                }
                try {
                    Class klass = Class.forName(qname);
                    if (PersistenceCapable.class.isAssignableFrom(klass)) {
                        classes.add(klass);
                    }
                } catch (ClassNotFoundException e) {
                    die("unable to load class " + qname + " for file " +
                        files[i]);
                }
            }
        }
    }

}
