package com.redhat.persistence.jdotest;

import com.redhat.persistence.*;
import com.redhat.persistence.engine.rdbms.*;
import com.redhat.persistence.pdl.*;
import com.redhat.persistence.metadata.*;

import java.io.*;
import java.lang.reflect.*;
import java.lang.reflect.Array;
import java.sql.*;
import java.util.*;

/**
 * Test
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/06/22 $
 **/

abstract class Test {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/redhat/persistence/jdotest/Test.java#2 $ by $Author: vadim $, $DateTime: 2004/06/22 14:02:37 $";

    private Session m_ssn;

    public Session getSession() {
        return m_ssn;
    }

    public void setSession(Session ssn) {
        m_ssn = ssn;
    }

    public static final void main(Class klass, String[] args) {
        if (args.length < 2) {
            System.err.println
                ("usage: " + klass.getName() +
                 " <jdbc> { load | unload | test } " +
                 "[ method_1 . . . method_n ]");
            System.exit(1);
        }

        String jdbc = args[0];

        try {
            final Connection conn = DriverManager.getConnection(jdbc);
            conn.setAutoCommit(false);
            PDL pdl = new PDL();
            String name = klass.getName().replace('.', '/') + ".pdl";
            InputStream is = klass.getClassLoader().getResourceAsStream(name);
            if (is != null) {
                pdl.load(new InputStreamReader(is), name);
            }

            Root root = new Root();
            pdl.emit(root);

            String command = args[1];
            if (command.equals("load")) {
                Schema.load(root, conn);
                conn.commit();
            } else if (command.equals("unload")) {
                Schema.unload(root, conn);
                conn.commit();
            } else if (command.equals("test")) {
                Schema.load(root, conn);
                try {
                    ConnectionSource src = new ConnectionSource() {
                        public Connection acquire() { return conn; }
                        public void release(Connection conn) {}
                    };

                    Engine engine = new RDBMSEngine(src, new PostgresWriter());
                    Session ssn = new Session(root, engine, new QuerySource());

                    if (args.length == 2) {
                        run(klass, ssn);
                    } else {
                        String[] nargs = new String[args.length - 2];
                        System.arraycopy(args, 2, nargs, 0, nargs.length);
                        run(klass, ssn, nargs);
                    }
                } finally {
                    conn.rollback();
                }
            } else {
                System.err.println("unrecognized command: " + command);
            }
        } catch (SQLException e) {
            RuntimeException rex = new IllegalStateException();
            rex.initCause(e);
            throw rex;
        }
    }

    private static boolean isTest(Method m) {
        return ((m.getModifiers() & Modifier.STATIC) == 0
                && m.getName().startsWith("test"));
    }

    private static void run(Class klass, Session ssn) {
        Method[] methods = klass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method m = methods[i];
            if (isTest(m)) {
                run(klass, m, ssn);
            }
        }
    }

    private static void run(Class klass, Session ssn, String[] tests) {
        for (int i = 0; i < tests.length; i++) {
            try {
                Method m = klass.getMethod(tests[i], null);
                if (!isTest(m)) {
                    System.err.println("not a test: " + tests[i]);
                    continue;
                }
                run(klass, m, ssn);
            } catch (NoSuchMethodException e) {
                System.err.println("no such method: " + tests[i]);
            }
        }
    }

    private static void run(Class klass, Method m, Session ssn) {
        try {
            Constructor c = klass.getConstructor(null);
            Test t = (Test) c.newInstance(null);
            t.setSession(ssn);
            System.out.print(m.getName() + ": ");
            Object result = m.invoke(t, null);
            String msg;
            if (result == null) {
                msg = "PASS";
            } else {
                msg = "PASS ==> " + result;
            }
            System.out.println(msg);
        } catch (InvocationTargetException e) {
            System.out.println
                ("FAILED ==> " + e.getTargetException().getMessage());
            e.getTargetException().printStackTrace(System.out);
        } catch (IllegalAccessException e) {
            System.out.println("ERROR ==> " + e.getMessage());
        } catch (NoSuchMethodException e) {
            System.out.println("ERROR ==> unable to find test constructor");
        } catch (InstantiationException e) {
            System.out.println("ERROR ==> unable to construct test");
        }
    }

    protected void fail(String message) {
        throw new Error(message);
    }

    protected void assertTrue(boolean b, String message) {
        if (!b) { fail(message); }
    }

    protected void assertEquals(Object o1, Object o2, String message) {
        if (o1 == null) { assertTrue(o2 == null, message); }
        if (o2 == null) { assertTrue(o1 == null, message); }
        if (o1.getClass().isArray() && o2.getClass().isArray()) {
            int l1 = Array.getLength(o1);
            int l2 = Array.getLength(o2);
            assertEquals(l1, l2, message);
            for (int i = 0; i < l1; i++) {
                assertEquals(Array.get(o1, i), Array.get(o2, i), message);
            }
        } else {
            assertTrue(o1.equals(o2) && o2.equals(o1), message);
        }
    }

    protected void assertEquals(int n1, int n2, String message) {
        assertTrue(n1 == n2, message);
    }

}
