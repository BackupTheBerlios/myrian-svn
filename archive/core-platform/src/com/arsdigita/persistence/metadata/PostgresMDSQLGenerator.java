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


import com.arsdigita.persistence.oql.Query;
import com.arsdigita.util.PriorityQueue;
import com.arsdigita.util.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import org.apache.log4j.Category;

/**
 * A interface that defines an API to automatically generate SQL queries based
 * on the metadata provided in the PDL files.  The primary interface is the 
 * generateSQL function, which will generate an event for an object type/event
 * type combination ( @see ObjectEvent ).
 *
 * @author <a href="mailto:randyg@alum.mit.edu">Randy Graebner</a>
 * @version $Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/PostgresMDSQLGenerator.java#4 $
 * @since 4.6.3
 */

class PostgresMDSQLGenerator extends BaseMDSQLGenerator {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/PostgresMDSQLGenerator.java#4 $ by $Author: rhs $, $DateTime: 2002/07/19 16:18:07 $";

    private static Category s_log = 
        Category.getInstance(PostgresMDSQLGenerator.class);

}
