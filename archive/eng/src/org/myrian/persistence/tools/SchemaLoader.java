package org.myrian.persistence.tools;

import org.myrian.util.jdbc.*;
import org.myrian.persistence.jdo.Extensions;

import java.io.*;
import java.sql.*;
import java.util.*;
import javax.jdo.*;
import javax.jdo.spi.*;

/**
 * SchemaLoader
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/10/04 $
 **/

public class SchemaLoader {


    private static void die(String message) {
        System.err.println(message);
        System.exit(1);
    }

    public static final void main(String[] argv) {
        try {
            run(argv);
        } catch (Exception e) {
            die(e.getMessage());
        }
    }

    private static void run(String[] argv) throws Exception {
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
                "<class_1> ... <class_n>");
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
        List nonpc = new ArrayList();
        for (Iterator it = args.iterator(); it.hasNext(); ) {
            Class klass = Class.forName((String) it.next());
            System.out.println("Found: " + klass);
            if (PersistenceCapable.class.isAssignableFrom(klass)) {
                classes.add(klass);
            } else {
                nonpc.add(klass);
            }
        }

        if (!nonpc.isEmpty()) {
            for (Iterator it = nonpc.iterator(); it.hasNext(); ) {
                System.err.println("not persistence capable: " + it.next());
            }
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

}
