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

import com.arsdigita.util.PriorityQueue;
import com.arsdigita.util.StringUtils;
import com.arsdigita.persistence.oql.Query;
import java.util.NoSuchElementException;

import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.log4j.Category;

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
 * @version $Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/OracleMDSQLGenerator.java#7 $
 * @since 4.6.3
 */
class OracleMDSQLGenerator extends BaseMDSQLGenerator {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/OracleMDSQLGenerator.java#7 $ by $Author: rhs $, $DateTime: 2002/07/19 16:18:07 $";

    private static final Category s_log =
        Category.getInstance(OracleMDSQLGenerator.class.getName());

}
