/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.arsdigita.persistence.pdl;

import java.util.Collection;

/**
 * The PDLFilter interface can be used to filter the contents of a
 * {@link PDLSource} based on filename extension and database suffix.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public interface PDLFilter {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/pdl/PDLFilter.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    /**
     * Tests the names for inclusion in the filtered results of a
     * PDLSource.
     *
     * @param names a collection of strings to test
     * @return the collection of accepted names
     **/

    Collection accept(Collection names);

}
