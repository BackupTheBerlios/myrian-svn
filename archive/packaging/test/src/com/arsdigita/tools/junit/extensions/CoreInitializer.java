package com.arsdigita.tools.junit.extensions;

import com.arsdigita.init.*;
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
 * @version $Revision: #1 $ $Date: 2003/09/24 $
 **/

public class CoreInitializer extends CompoundInitializer {

    public final static String versionId = "$Id: //core-platform/test-packaging/test/src/com/arsdigita/tools/junit/extensions/CoreInitializer.java#1 $ by $Author: dennis $, $DateTime: 2003/09/24 22:52:36 $";

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

    private static class StringArrayParameter extends StringParameter {
        private final StringArrayConverter m_converter;

        StringArrayParameter(final String name,
                             final int multiplicity,
                             final Object defaalt) {
            super(name, multiplicity, defaalt);

            m_converter = new StringArrayConverter();
        }

        protected Object unmarshal(final String literal, final List errors) {
            final String[] literals = StringUtils.split(literal, ',');
            final String[] strings = new String[literals.length];

            for (int i = 0; i < literals.length; i++) {
                final String elem = literals[i];

                strings[i] = (String) super.unmarshal(elem, errors);

                if (!errors.isEmpty()) {
                    break;
                }
            }
            return strings;
        }

        protected void validate(final Object value, final List errors) {
            if (value != null) {
                final String[] strings = (String[]) value;

                for (int i = 0; i < strings.length; i++) {
                    super.validate(strings[i], errors);

                    if (!errors.isEmpty()) {
                        break;
                    }
                }
            }
        }
    }
}
