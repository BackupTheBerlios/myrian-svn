package com.arsdigita.tools.junit.extensions;

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
 * @version $Revision: #3 $ $Date: 2004/01/19 $
 **/

public class CoreInitializer extends CompoundInitializer {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/tools/junit/extensions/CoreInitializer.java#3 $ by $Author: jorris $, $DateTime: 2004/01/19 08:16:18 $";

    private static final Logger s_log = Logger.getLogger
        (CoreInitializer.class);

    private static final Parameter s_pdl = new StringArrayParameter
        ("waf.runtime.test.pdl", Parameter.OPTIONAL, new String[0]);

    public CoreInitializer() {
        String[] pdlManifests = (String[])SystemProperties.get(s_pdl);
        for (int i = 0; i < pdlManifests.length; i++) {
            s_log.debug("Adding test PDL manifest: " + pdlManifests[i]);
            add(new PDLInitializer
                (new ManifestSource(pdlManifests[i], new NameFilter("pg", "pdl"))));
        }
    }

}
