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
 * A class that provides an API to automatically generate SQL queries based
 * on the metadata provided in the PDL files.  The primary interface is the
 * generateSQL function, which will generate an event for an object type/event
 * type combination ( @see Event ).
 *
 * Be aware that there are some restrictions on the use of this class.
 * First, it will not work for objects that have an object key composed of
 * more than one element.  Also, the RETRIEVE and RETRIEVE_ALL event
 * generators require that all of the attributes in the type hierarchy have
 * columns defined, not just the current object type (INSERT, UPDATE, and
 * DELETE do not have this restriction).  These restrictions may be removed
 * in the future, but we do not consider them to be essential at the moment.
 *
 * @author <a href="mailto:pmcneill@arsdigita.com">Patrick McNeill</a>
 * @version $Id: //core-platform/proto/src/com/arsdigita/persistence/metadata/OracleMDSQLGenerator.java#1 $
 * @since 4.6.3
 */
class OracleMDSQLGenerator extends BaseMDSQLGenerator {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/metadata/OracleMDSQLGenerator.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    private static final Logger s_log =
        Logger.getLogger(OracleMDSQLGenerator.class.getName());

}
