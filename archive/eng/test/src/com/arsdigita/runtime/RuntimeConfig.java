package com.arsdigita.runtime;

/**
 * RuntimeConfig
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/06/07 $
 **/

public class RuntimeConfig {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/arsdigita/runtime/RuntimeConfig.java#1 $ by $Author: rhs $, $DateTime: 2004/06/07 13:49:55 $";

    private static final RuntimeConfig CONFIG = new RuntimeConfig();

    public static RuntimeConfig getConfig() {
        return CONFIG;
    }

    public String getJDBCURL() {
        return "jdbc:postgresql:rafaels?user=rafaels";
    }

}
