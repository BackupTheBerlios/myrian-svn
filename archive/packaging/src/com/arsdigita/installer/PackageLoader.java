package com.arsdigita.installer;

import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.db.DbHelper;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.sql.*;
import java.util.jar.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * PackageLoader
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/09/17 $
 **/

public class PackageLoader {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/installer/PackageLoader.java#2 $ by $Author: rhs $, $DateTime: 2003/09/17 15:39:02 $";

    private static final Logger s_log =
        Logger.getLogger(PackageLoader.class);

    private final URL m_pkg;
    private final URLClassLoader m_cload;

    private String m_schemaLoad = null;
    private String m_dataLoad = null;

    private String m_schemaUpgrade = null;
    private String m_dataUpgrade = null;

    private String m_dataUnload = null;
    private String m_schemaUnload = null;

    private PackageLoader(URL pkg) {
        m_pkg = pkg;
        m_cload = URLClassLoader.newInstance(new URL[] {m_pkg});

        final JarInputStream jis;
        try { jis = new JarInputStream(m_pkg.openStream()); }
        catch (IOException e) { throw new UncheckedWrapperException(e); }

        try {
            Manifest man = jis.getManifest();
            if (man == null) { return; }
            Attributes attrs = man.getMainAttributes();
            if (attrs == null) { return; }

            m_schemaLoad = attrs.getValue("WAF-Schema-Load");
            m_dataLoad = attrs.getValue("WAF-Data-Load");

            m_schemaUpgrade = attrs.getValue("WAF-Schema-Upgrade");
            m_dataUpgrade = attrs.getValue("WAF-Data-Upgrade");

            m_dataUnload = attrs.getValue("WAF-Data-Unload");
            m_schemaUnload = attrs.getValue("WAF-Schema-Unload");
        } finally {
            try { jis.close(); }
            catch (IOException e) { throw new UncheckedWrapperException(e); }
        }
    }

    private void loadSchema(Connection conn) {
        if (m_schemaLoad == null) { return; }

        SQLLoader loader = new SQLLoader(conn) {
            protected Reader open(String name) {
                InputStream is = m_cload.getResourceAsStream(name);
                if (is == null) {
                    return null;
                } else {
                    print("Loading: " + name);
                    return new InputStreamReader(is);
                }
            }
        };

        loader.load(m_schemaLoad);
        print("Loading: Done");
        System.out.println();
    }

    private Object invoke(String className, String method, Object[] args) {
        try {
            final Class[] params = new Class[] { String[].class };
            Class klass = Class.forName(className, true, m_cload);
            Method main = klass.getMethod(method, params);
            return main.invoke(null, args);
        } catch (ClassNotFoundException e) {
            throw new UncheckedWrapperException(e);
        } catch (NoSuchMethodException e) {
            throw new UncheckedWrapperException(e);
        } catch (IllegalAccessException e) {
            throw new UncheckedWrapperException(e);
        } catch (InvocationTargetException e) {
            throw new UncheckedWrapperException(e);
        }
    }

    private void loadData(String[] args) {
        if (m_dataLoad == null) { return; }
        System.out.println("Initializing Schema:");
        invoke(m_dataLoad, "main", new Object[] {args});
    }

    private static final void print(String str) {
        String padded = "\r" + str;
        for (int i = padded.length(); i < 80; i++) {
            padded = padded + " ";
        }
        System.out.print(padded);
        System.out.flush();
    }

    public static final void main(String[] args) throws Exception {
        BasicConfigurator.configure();

        if (args.length != 4) {
            System.err.println
                ("Usage: PackageLoader <JDBC_URL> <username> " +
                 "<password> <package_url>");
            System.exit(1);
        }

        String jdbc = args[0];
        String user = args[1];
        String password = args[2];
        String url = args[3];

        final Connection conn;

        try {
            int db = DbHelper.getDatabaseFromURL(jdbc);

            switch (db) {
                case DbHelper.DB_POSTGRES:
                    Class.forName("org.postgresql.Driver");
                    break;
                case DbHelper.DB_ORACLE:
                    Class.forName("oracle.jdbc.driver.OracleDriver");
                    break;
                default:
                    throw new IllegalArgumentException("unsupported database");
            }

            System.out.println("Database: " + DbHelper.getDatabaseName(db));
            conn = DriverManager.getConnection(jdbc, user, password);
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            throw new UncheckedWrapperException(e);
        } catch (ClassNotFoundException e) {
            throw new UncheckedWrapperException(e);
        }

        try {
            PackageLoader loader = new PackageLoader(new URL(url));
            loader.loadSchema(conn);
            loader.loadData(args);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        try {
            conn.commit();
            conn.close();
        } catch (SQLException e) {
            throw new UncheckedWrapperException(e);
        }
    }

}
