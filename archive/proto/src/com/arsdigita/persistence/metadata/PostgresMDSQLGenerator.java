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

/**
 * A interface that defines an API to automatically generate SQL queries based
 * on the metadata provided in the PDL files.  The primary interface is the
 * generateSQL function, which will generate an event for an object type/event
 * type combination ( @see ObjectEvent ).
 *
 * @author <a href="mailto:randyg@alum.mit.edu">Randy Graebner</a>
 * @version $Id: //core-platform/proto/src/com/arsdigita/persistence/metadata/PostgresMDSQLGenerator.java#1 $
 * @since 4.6.3
 */

class PostgresMDSQLGenerator extends BaseMDSQLGenerator {

    public static final String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/metadata/PostgresMDSQLGenerator.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    private static final Logger s_log =
        Logger.getLogger(PostgresMDSQLGenerator.class);

}
