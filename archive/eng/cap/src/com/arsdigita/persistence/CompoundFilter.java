/*
 * Copyright (C) 2001-2004 Red Hat, Inc.  All Rights Reserved.
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
package com.arsdigita.persistence;


/**
 * CompoundFilters are used to AND or OR multiple filters together.
 *
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 */

public interface CompoundFilter extends Filter {

    String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/CompoundFilter.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";


    /**
     *  This provides a mechanism for adding conditions to the existing
     *  filter.  This appends the passed in conditions to the existing
     *  conditions with an "and" statement.
     *  There is no way to remove conditions.
     *
     *  @param conditions The conditions to add to this filter
     */
    CompoundFilter addFilter(String conditions);


    /**
     *  This adds the passed in filter to this query and adds it
     *  according to the type of filter this is (if it was created
     *  using Filter.or() then it ORs this filter with the existing
     *  ones; otherwise it ANDs it);
     *
     *  @return this
     */
    CompoundFilter addFilter(Filter filter);

    /**
     * Removes the passed in filter if it was directly
     * added to this compound filter.
     */
    boolean removeFilter(Filter filter);
}
