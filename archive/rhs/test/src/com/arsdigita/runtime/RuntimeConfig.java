package com.arsdigita.runtime;

/**
 * RuntimeConfig
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/05/02 $
 **/

public class RuntimeConfig {

    public final static String versionId = "$Id: //users/rhs/persistence/test/src/com/arsdigita/runtime/RuntimeConfig.java#1 $ by $Author: rhs $, $DateTime: 2004/05/02 13:12:27 $";

    private static final RuntimeConfig CONFIG = new RuntimeConfig();

    public static RuntimeConfig getConfig() {
        return CONFIG;
    }

    public String getJDBCURL() {
        return "jdbc:postgresql:rafaels?user=rafaels";
    }

}
