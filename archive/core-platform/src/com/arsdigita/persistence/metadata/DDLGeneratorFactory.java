/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence.metadata;

/**
 * A factory class that instantiates a DDLGenerator implementation and then
 * returns it to calling classes.
 * 
 * @author <a href="mailto:randyg@alum.mit.edu">Randy Graebner</a>
 * @version $Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/DDLGeneratorFactory.java#1 $
 * @since 4.6.3
 */
public class DDLGeneratorFactory {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/DDLGeneratorFactory.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";
    private static DDLGenerator s_generator = null;

    public static void setDDLGenerator(String impl) {
        if (impl == null) {
            impl = "com.arsdigita.persistence.metadata.OracleDDLGenerator";
        }

        try {
            s_generator = (DDLGenerator)Class.forName(impl).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Could not instantiate " +
                                               "DDL generator");
        }
    }

    public static DDLGenerator getInstance() {
        if (s_generator == null) {
            setDDLGenerator(null);
        }

        return s_generator;
    }
}
