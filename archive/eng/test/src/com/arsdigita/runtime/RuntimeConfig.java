/*
 * Copyright (C) 2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.arsdigita.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * RuntimeConfig
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/08/30 $
 **/

public class RuntimeConfig {
    private final static String PROPERTIES = "rhs.properties";
    private final static String JDBC_URL = "jdbc.url";

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/arsdigita/runtime/RuntimeConfig.java#3 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
