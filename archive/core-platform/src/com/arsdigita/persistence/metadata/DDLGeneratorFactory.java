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
 * A factory class that instantiates a DDLGenerator implementation and then
 * returns it to calling classes.
 *
 * @author <a href="mailto:randyg@alum.mit.edu">Randy Graebner</a>
 * @version $Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/DDLGeneratorFactory.java#6 $
 * @since 4.6.3
 */
public class DDLGeneratorFactory {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/DDLGeneratorFactory.java#6 $ by $Author: dennis $, $DateTime: 2002/08/14 23:39:40 $";

    private static final Logger s_log =
        Logger.getLogger(DDLGeneratorFactory.class);

    private static DDLGenerator s_generator = null;

    private static String ORACLE_GENERATOR =
        "com.arsdigita.persistence.metadata.OracleDDLGenerator";
    private static String POSTGRES_GENERATOR =
        "com.arsdigita.persistence.metadata.PostgresDDLGenerator";

    public static void setDDLGenerator(String impl) {
        if (impl == null) {
            impl = ORACLE_GENERATOR;
        }

        try {
            s_generator = (DDLGenerator)Class.forName(impl).newInstance();
        } catch (Exception e) {
            s_log.error("Error creating generator: " + impl, e);
            throw new IllegalArgumentException("Could not instantiate " +
                                               "DDL generator");
        }
    }

    public static DDLGenerator getInstance() {
        if (s_generator == null) {
            if (DbHelper.getDatabase() == DbHelper.DB_ORACLE) {
                setDDLGenerator(ORACLE_GENERATOR);
            } else if (DbHelper.getDatabase() == DbHelper.DB_POSTGRES) {
                setDDLGenerator(POSTGRES_GENERATOR);
            } else {
                setDDLGenerator(null);
            }
        }

        return s_generator;
    }
}
