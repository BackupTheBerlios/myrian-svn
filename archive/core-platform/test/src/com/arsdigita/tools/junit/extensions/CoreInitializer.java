/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
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
 * @version $Revision: #5 $ $Date: 2004/03/30 $
 **/

public class CoreInitializer extends CompoundInitializer {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/tools/junit/extensions/CoreInitializer.java#5 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

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
