package com.arsdigita.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * RuntimeConfig
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/06/17 $
 **/

public class RuntimeConfig {
    private final static String PROPERTIES = "rhs.properties";
    private final static String JDBC_URL = "jdbc.url";

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/arsdigita/runtime/RuntimeConfig.java#2 $ by $Author: vadim $, $DateTime: 2004/06/17 11:58:17 $";

    private static final RuntimeConfig CONFIG = new RuntimeConfig();

    public static RuntimeConfig getConfig() {
        return CONFIG;
    }

    public String getJDBCURL() {
        InputStream is =
            getClass().getClassLoader().getResourceAsStream(PROPERTIES);

        if (is == null) {
            throw new IllegalStateException
                ("Couldn' find " + PROPERTIES + " anywhere on the classpath.");
        }

        Properties props = new Properties();
        try {
            props.load(is);
        } catch (IOException ex) {
            RuntimeException rex =
                new IllegalStateException("can't load " + PROPERTIES);
            rex.initCause(ex);
            throw rex;
        }
        return props.getProperty(JDBC_URL,
                                 "jdbc:postgresql:rafaels?user=rafaels");
    }
}
