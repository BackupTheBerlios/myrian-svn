package com.arsdigita.tools.junit.extensions;

import com.arsdigita.db.DbHelper;
import com.arsdigita.runtime.*;
import com.arsdigita.persistence.pdl.*;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.SystemProperties;
import com.arsdigita.util.parameter.*;
import java.util.List;
import org.apache.commons.beanutils.converters.*;
import org.apache.log4j.Logger;

/**
 * CoreInitializer
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/01/28 $
 **/

public class CoreInitializer extends CompoundInitializer {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/tools/junit/extensions/CoreInitializer.java#4 $ by $Author: ashah $, $DateTime: 2004/01/28 11:54:40 $";

    private static final Logger s_log = Logger.getLogger
        (CoreInitializer.class);

    private static final Parameter s_pdl = new StringArrayParameter
        ("waf.runtime.test.pdl", Parameter.OPTIONAL, new String[0]);

    public CoreInitializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final String db = DbHelper.getDatabaseSuffix
            (DbHelper.getDatabaseFromURL(url));

        String[] pdlManifests = (String[])SystemProperties.get(s_pdl);
        for (int i = 0; i < pdlManifests.length; i++) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl.getResourceAsStream(pdlManifests[i]) == null) { continue; }
            s_log.debug("Adding test PDL manifest: " + pdlManifests[i]);
            add(new PDLInitializer
                (new ManifestSource
                 (pdlManifests[i], new NameFilter(db, "pdl"))));
        }
    }

}
