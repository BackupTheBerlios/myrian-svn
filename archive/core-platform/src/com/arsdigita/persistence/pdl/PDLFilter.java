/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.arsdigita.persistence.pdl;

import java.util.Collection;

/**
 * The PDLFilter interface can be used to filter the contents of a
 * {@link PDLSource} based on filename extension and database suffix.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/04/07 $
 **/

public interface PDLFilter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/PDLFilter.java#5 $ by $Author: dennis $, $DateTime: 2004/04/07 16:07:11 $";

    /**
     * Tests the names for inclusion in the filtered results of a
     * PDLSource.
     *
     * @param names a collection of strings to test
     * @return the collection of accepted names
     **/

    Collection accept(Collection names);

}
