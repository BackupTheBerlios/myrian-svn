/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence.metadata;

import org.apache.log4j.Logger;
import com.arsdigita.db.DbHelper;

/**
 * A factory class that instantiates a MDSQLGenerator implementation and then
 * returns it to calling classes.
 *
 * @author <a href="mailto:pmcneill@arsdigita.com">Patrick McNeill</a>
 * @version $Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/MDSQLGeneratorFactory.java#6 $
 * @since 4.6.3
 */
public class MDSQLGeneratorFactory {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/MDSQLGeneratorFactory.java#6 $ by $Author: dennis $, $DateTime: 2002/08/14 23:39:40 $";

    private static final Logger s_log =
        Logger.getLogger(MDSQLGeneratorFactory.class);

    private static MDSQLGenerator s_generator = null;

    public static String ORACLE_GENERATOR =
        "com.arsdigita.persistence.metadata.OracleMDSQLGenerator";
    public static String POSTGRES_GENERATOR =
        "com.arsdigita.persistence.metadata.PostgresMDSQLGenerator";

    public static void setMDSQLGenerator(String impl) {
        if (impl == null) {
            impl = ORACLE_GENERATOR;
        }

        try {
            s_generator = (MDSQLGenerator)Class.forName(impl).newInstance();
        } catch (Exception e) {
            s_log.error("Error instantiation the MDSQL Generator: " + impl, e);
            throw new IllegalArgumentException("Could not instantiate " +
                                               "MDSQL generator");
        }
    }

    public static MDSQLGenerator getInstance() {
        if (s_generator == null) {
            if (DbHelper.getDatabase() == DbHelper.DB_ORACLE) {
                setMDSQLGenerator(ORACLE_GENERATOR);
            } else if (DbHelper.getDatabase() == DbHelper.DB_POSTGRES) {
                setMDSQLGenerator(POSTGRES_GENERATOR);
            } else {
                setMDSQLGenerator(null);
            }
        }

        return s_generator;
    }
}
