/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.arsdigita.persistence;


/**
 * CompoundFilters are used to AND or OR multiple filters together.
 *
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Revision: #5 $ $Date: 2004/03/30 $
 */

public interface CompoundFilter extends Filter {

    String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/CompoundFilter.java#5 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";


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
